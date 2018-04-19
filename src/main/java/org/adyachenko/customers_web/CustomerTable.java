package org.adyachenko.customers_web;

import java.util.ArrayList;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;

import customer_core.service.CustomerDataService;
import customers_core.db.CustomerDB;
import customers_core.db.CustomerStatusDB;

public class CustomerTable extends VerticalLayout {

	private static final String TABLE_FILTER_STYLE = "customer-table-filter-text";
	private static final String LAST_NAME_FILTER = "LastNameFilter";
	private static final String FIRST_NAME_FILTER = "FirstNameFilter";
	private static final String CUSTOMER_STATUS_FILTER = "CustomerStatusFilter";
	private static final long serialVersionUID = 6514622108946607383L;
	private Grid<CustomerDB> customerTable;

	public CustomerTable() {
		this.addStyleName("customers-table-layout");
		this.build();
	}

	private void build() {

		CustomerDataService customerDataService = new CustomerDataService();

		customerTable = new Grid<>();
		customerTable.setSizeFull();
		HeaderRow filterRow = customerTable.appendHeaderRow();

		ArrayList<CustomerDB> allCustomers = customerDataService.getAllCustomers();
		customerTable.setItems(allCustomers);
		customerTable.addColumn(CustomerDB::getId).setCaption("Id");

		Column<CustomerDB, ?> firstNameColumn = customerTable.addColumn(CustomerDB::getFirstName)
				.setCaption("First Name");
		Column<CustomerDB, ?> lastNameColumn = customerTable.addColumn(CustomerDB::getLastName).setCaption("Last Name");
		Column<CustomerDB, ?> statusColumn = customerTable
				.addColumn(customer -> customer.getCustomerStatus().getStatusName()).setCaption("Status");

		Column<CustomerDB, ?> dateColumn = customerTable.addColumn(CustomerDB::getCreated).setCaption("Added Date");

		TextField firstNameFilter = new TextField();
		firstNameFilter.setId(FIRST_NAME_FILTER);
		firstNameFilter.addStyleName(TABLE_FILTER_STYLE);
		firstNameFilter.setPlaceholder("First Name...");

		ArrayList<AbstractComponent> filterComponents = new ArrayList<>();

		TextField lastNameFilter = new TextField();
		lastNameFilter.addStyleName(TABLE_FILTER_STYLE);
		lastNameFilter.setPlaceholder("Last Name...");
		lastNameFilter.setId(LAST_NAME_FILTER);

		firstNameFilter.addValueChangeListener(new ValueChangeListener<String>() {

			private static final long serialVersionUID = -5260840568446184561L;

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				@SuppressWarnings("unchecked")
				ListDataProvider<CustomerDB> dataProvider = (ListDataProvider<CustomerDB>) customerTable
						.getDataProvider();

				filterTableData(dataProvider, filterComponents);

			}
		});

		lastNameFilter.addValueChangeListener(getFilterTableListener(filterComponents));

		ArrayList<CustomerStatusDB> customerStatuses = customerDataService.getCustomerStatuses();
		ComboBox<CustomerStatusDB> customerStatusFilter = new ComboBox<CustomerStatusDB>();
		customerStatusFilter.setCaption("Customer Status");
		customerStatusFilter.setItems(customerStatuses);
		customerStatusFilter.setItemCaptionGenerator(CustomerStatusDB::getStatusName);
		customerStatusFilter.setId(CUSTOMER_STATUS_FILTER);
		customerStatusFilter.addStyleName(TABLE_FILTER_STYLE);

		customerStatusFilter.addValueChangeListener(new ValueChangeListener<CustomerStatusDB>() {

			private static final long serialVersionUID = 8157010827221095855L;

			@Override
			public void valueChange(ValueChangeEvent<CustomerStatusDB> event) {

				@SuppressWarnings("unchecked")
				ListDataProvider<CustomerDB> dataProvider = (ListDataProvider<CustomerDB>) customerTable
						.getDataProvider();

				filterTableData(dataProvider, filterComponents);

			}
		});

		filterComponents.add(firstNameFilter);
		filterComponents.add(lastNameFilter);
		filterComponents.add(customerStatusFilter);

		filterRow.getCell(firstNameColumn).setComponent(firstNameFilter);
		filterRow.getCell(lastNameColumn).setComponent(lastNameFilter);
		filterRow.getCell(statusColumn).setComponent(customerStatusFilter);

		customerTable.addItemClickListener(event -> {

			Window customerView = new Window();
			CustomerDB customer = event.getItem();
			customerView.setCaption("Customer " + customer.getFirstName() + " " + customer.getLastName());
			customerView.setWidth("750");
			customerView.center();
			customerView.setPositionY(50);
			customerView.setPositionX(250);
			customerView.setContent(new CustomerForm(customerView, this, customer));
			UI.getCurrent().addWindow(customerView);

		});

		this.addComponent(customerTable);

	}

	private ValueChangeListener<String> getFilterTableListener(ArrayList<AbstractComponent> filterComponents) {
		return new ValueChangeListener<String>() {

			private static final long serialVersionUID = -5260840568446184561L;

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				@SuppressWarnings("unchecked")
				ListDataProvider<CustomerDB> dataProvider = (ListDataProvider<CustomerDB>) customerTable
						.getDataProvider();

				filterTableData(dataProvider, filterComponents);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void filterTableData(ListDataProvider<CustomerDB> dataProvider,
			ArrayList<AbstractComponent> filterComponents) {

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
			}
			dataProvider.refreshAll();

		}

	}

	private boolean statusEquals(CustomerStatusDB where, CustomerStatusDB what) {
		if (where != null && what != null)
			return (where.getId().equals(what.getId()));
		return true;
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		if (where != null)
			return where.toLowerCase().contains(what.toLowerCase());
		else
			return false;
	}

	public void refreshTable() {
		CustomerDataService customerDataService = new CustomerDataService();

		ArrayList<CustomerDB> allCustomers = customerDataService.getAllCustomers();
		customerTable.setItems(allCustomers);
		customerTable.getDataProvider().refreshAll();
	}
}
