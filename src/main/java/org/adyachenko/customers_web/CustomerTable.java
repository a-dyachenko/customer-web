package org.adyachenko.customers_web;

import java.util.ArrayList;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;

import customer_core.service.CustomerDataService;
import customers_core.db.CustomerDB;

public class CustomerTable extends VerticalLayout {

	private static final String LAST_NAME_FILTER = "LastNameFilter";
	private static final String FIRST_NAME_FILTER = "FirstNameFilter";
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
		customerTable.addColumn(customer -> customer.getCustomerStatus().getStatusName()).setCaption("Status");
		
		Column<CustomerDB, ?> dateColumn = customerTable.addColumn(CustomerDB::getCreated).setCaption("Added Date");
		
		TextField firstNameFilter = new TextField();
		firstNameFilter.setId(FIRST_NAME_FILTER);
		firstNameFilter.addStyleName("customer-table-filter-text");
		firstNameFilter.setPlaceholder("Search First Name...");

		ArrayList<AbstractField> filterComponents = new ArrayList<>();

		filterRow.getCell(firstNameColumn).setComponent(firstNameFilter);

		TextField lastNameFilter = new TextField();
		lastNameFilter.addStyleName("customer-table-filter-text");
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

		lastNameFilter.addValueChangeListener(new ValueChangeListener<String>() {

			private static final long serialVersionUID = -5260840568446184561L;

			@Override
			public void valueChange(ValueChangeEvent<String> event) {
				@SuppressWarnings("unchecked")
				ListDataProvider<CustomerDB> dataProvider = (ListDataProvider<CustomerDB>) customerTable
						.getDataProvider();

				filterTableData(dataProvider, filterComponents);
			}
		});

		filterComponents.add(firstNameFilter);
		filterComponents.add(lastNameFilter);
		filterRow.getCell(lastNameColumn).setComponent(lastNameFilter);

		customerTable.addItemClickListener(event -> {
			Window customerView = new Window();
			CustomerDB customer = event.getItem();
			customerView.setCaption("Customer " + customer.getFirstName() + " " + customer.getLastName());
			customerView.setWidth("700");
			customerView.center();
			customerView.setPositionY(50);
			customerView.setPositionX(250);
			customerView.setContent(new ViewCustomerForm(customer, this));

			UI.getCurrent().addWindow(customerView);
		});

		this.addComponent(customerTable);

	}

	private void filterTableData(ListDataProvider<CustomerDB> dataProvider, ArrayList<AbstractField> filterComponents) {

		for (AbstractField<?> component : filterComponents) {

			if (component.getId().equals(FIRST_NAME_FILTER)) {
				dataProvider.addFilter(CustomerDB::getFirstName,
						s -> caseInsensitiveContains(s, component.getValue().toString()));
			} else if (component.getId().equals(LAST_NAME_FILTER)) {
				dataProvider.addFilter(CustomerDB::getLastName,
						s -> caseInsensitiveContains(s, component.getValue().toString()));
			}

			dataProvider.refreshAll();

		}

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
