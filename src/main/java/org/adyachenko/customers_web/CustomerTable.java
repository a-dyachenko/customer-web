package org.adyachenko.customers_web;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;

import customer_core.service.CustomerDataService;
import customers_core.dao.CustomerCoreSessionProvider;
import customers_core.db.CustomerDB;
import customers_core.db.CustomerStatusDB;

public class CustomerTable extends VerticalLayout {

	private static final String FILTER_PLACEHOLDER_FIRST_NAME = "First Name...";
	private static final String FILTER_PLACEHOLDER_LAST_NAME = "Last Name...";
	private static final String FILTER_PLACEHOLDER_CUSTOMER_STATUS = "Customer Status";
	private static final String FILTER_PLACEHOLDER_DATE_TIME_AFTER = "Date/Time (after)";

	private static final String LAST_NAME_FILTER = "LastNameFilter";
	private static final String FIRST_NAME_FILTER = "FirstNameFilter";
	private static final String CUSTOMER_STATUS_FILTER = "CustomerStatusFilter";
	private static final String DATE_TIME_FILTER = "DateTimeFilter";

	private static final String STYLE_TABLE_FILTER = "customer-table-filter-text";
	private static final String STYLE_TABLE_DATETIME_FILTER = "customer-table-filter-datetime";
	private static final String STYLE_CUSTOMERS_TABLE_LAYOUT = "customers-table-layout";
	private CustomerDataService customerDataService;
	private static final long serialVersionUID = 6514622108946607383L;

	List<AbstractComponent> filterComponents;

	private Grid<CustomerDB> customerTable;

	public CustomerTable() {
		this.addStyleName(STYLE_CUSTOMERS_TABLE_LAYOUT);
		this.build();
	}

	private void build() {

		filterComponents = new ArrayList<AbstractComponent>();
		customerTable = new Grid<>();
		customerTable.setSizeFull();
		customerDataService = CustomerDataService.getCustomerDataService(new CustomerCoreSessionProvider());

		HeaderRow filterRow = customerTable.appendHeaderRow();

		List<CustomerDB> allCustomers = customerDataService.getAllCustomers();
		customerTable.setItems(allCustomers);
		customerTable.addColumn(CustomerDB::getId).setCaption(CustomerConstants.LABEL_ID);

		/// columns 
		
		Column<CustomerDB, ?> firstNameColumn = customerTable.addColumn(CustomerDB::getFirstName)
				.setCaption(CustomerConstants.LABEL_FIRST_NAME);
		Column<CustomerDB, ?> lastNameColumn = customerTable.addColumn(CustomerDB::getLastName)
				.setCaption(CustomerConstants.LABEL_LAST_NAME);
		Column<CustomerDB, ?> statusColumn = customerTable
				.addColumn(customer -> customer.getCustomerStatus().getStatusName())
				.setCaption(CustomerConstants.LABEL_STATUS);
		Column<CustomerDB, Date> dateTimeColumn = customerTable.addColumn(CustomerDB::getCreated)
				.setCaption(CustomerConstants.LABEL_ADDED_DATE);

		/// set column filters 
		TextField firstNameFilter = getTextTableFilter(FILTER_PLACEHOLDER_FIRST_NAME, FIRST_NAME_FILTER); 
		filterComponents.add(firstNameFilter);
		filterRow.getCell(firstNameColumn).setComponent(firstNameFilter);
		
		TextField lastNameFilter = getTextTableFilter(FILTER_PLACEHOLDER_LAST_NAME, LAST_NAME_FILTER); 
		filterComponents.add(lastNameFilter);
		filterRow.getCell(lastNameColumn).setComponent(lastNameFilter);
		
		ComboBox<CustomerStatusDB> customerStatusFilter = getCustomerStatusFilter(); 
		filterComponents.add(customerStatusFilter);
		filterRow.getCell(statusColumn).setComponent(customerStatusFilter);
		
		DateField dateTimeFilter = getDateTimeFilter(); 
		filterComponents.add(dateTimeFilter); 
		filterRow.getCell(dateTimeColumn).setComponent(dateTimeFilter);

		customerTable.addItemClickListener(event -> {

			CustomerDB customer = event.getItem();
			CustomerWindow.popupCustomerWindow(this, customer);

		});

		this.addComponent(customerTable);

	}

	private DateField getDateTimeFilter() {
		DateField dateTimeFilter = new DateField();
		dateTimeFilter.setCaption(FILTER_PLACEHOLDER_DATE_TIME_AFTER);
		dateTimeFilter.setId(DATE_TIME_FILTER);
		dateTimeFilter.addStyleName(STYLE_TABLE_DATETIME_FILTER);

		dateTimeFilter.addValueChangeListener(new ValueChangeListener<LocalDate>() {

			private static final long serialVersionUID = 5514635511759845516L;

			@Override
			public void valueChange(ValueChangeEvent<LocalDate> event) {

				filterTableData();
			}
		});
		return dateTimeFilter;
	}

	private ComboBox<CustomerStatusDB> getCustomerStatusFilter() {
		ComboBox<CustomerStatusDB> customerStatusFilter = new ComboBox<CustomerStatusDB>();
		List<CustomerStatusDB> customerStatuses = customerDataService.getCustomerStatuses();
		customerStatusFilter.setCaption(FILTER_PLACEHOLDER_CUSTOMER_STATUS);
		customerStatusFilter.setItems(customerStatuses);
		customerStatusFilter.setItemCaptionGenerator(CustomerStatusDB::getStatusName);
		customerStatusFilter.setId(CUSTOMER_STATUS_FILTER);
		customerStatusFilter.addStyleName(STYLE_TABLE_FILTER);

		customerStatusFilter.addValueChangeListener(new ValueChangeListener<CustomerStatusDB>() {

			private static final long serialVersionUID = 8157010827221095855L;

			@Override
			public void valueChange(ValueChangeEvent<CustomerStatusDB> event) {

				filterTableData();
			}
		});
		return customerStatusFilter;
	}

	private TextField getTextTableFilter(String placeholder, String filterId) {
		TextField textTableFilter = new TextField();
		textTableFilter.addStyleName(STYLE_TABLE_FILTER);
		textTableFilter.setPlaceholder(placeholder);
		textTableFilter.setId(filterId);

		textTableFilter.addValueChangeListener(new ValueChangeListener<String>() {
			private static final long serialVersionUID = -5260840568446184561L;

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				filterTableData();
			}
		});
		return textTableFilter;
	}

	private void filterTableData() {

		@SuppressWarnings("unchecked")
		ListDataProvider<CustomerDB> dataProvider = (ListDataProvider<CustomerDB>) customerTable.getDataProvider();

		for (AbstractComponent component : filterComponents) {

			String componentId = component.getId();

			if (componentId.equals(FIRST_NAME_FILTER)) {
				dataProvider.addFilter(CustomerDB::getFirstName,
						s -> caseInsensitiveContains(s, ((TextField) component).getValue().toString()));
			} else if (componentId.equals(LAST_NAME_FILTER)) {
				dataProvider.addFilter(CustomerDB::getLastName,
						s -> caseInsensitiveContains(s, ((TextField) component).getValue().toString()));
			} else if (componentId.equals(CUSTOMER_STATUS_FILTER)) {
				dataProvider.addFilter(CustomerDB::getCustomerStatus,
						s -> statusEquals(s, ((ComboBox<CustomerStatusDB>) component).getValue()));
			} else if (componentId.equals(DATE_TIME_FILTER)) {
				dataProvider.addFilter(CustomerDB::getCreated,
						s -> dateTimeAfter(s, ((DateField) component).getValue()));
			}

			dataProvider.refreshAll();
		}

	}

	private Boolean statusEquals(CustomerStatusDB where, CustomerStatusDB what) {
		if (where != null && what != null)
			return (where.getId() == (what.getId()));
		return true;
	}

	private Boolean dateTimeAfter(Date where, LocalDate localDate) {
		if (where != null && localDate != null)
			return where.after(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		else
			return true;
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		if (where != null)
			return where.toLowerCase().contains(what.toLowerCase());
		else
			return false;
	}

	public void refreshTable() {
		List<CustomerDB> allCustomers = customerDataService.getAllCustomers();
		customerTable.setItems(allCustomers);
		customerTable.getDataProvider().refreshAll();
	}
}
