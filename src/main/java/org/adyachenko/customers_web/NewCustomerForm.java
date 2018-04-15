package org.adyachenko.customers_web;

import java.util.ArrayList;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import customer_core.service.CustomerDataService;
import customers_core.db.CustomerDB;
import customers_core.db.CustomerStatusDB;

public class NewCustomerForm extends VerticalLayout {

	private static final long serialVersionUID = 2899390100026188322L;

	private Window containerWindow;
	private CustomerTable customerTable;

	public NewCustomerForm(Window containerWindow) {
		this.containerWindow = containerWindow;
		this.buildForm();
	}

	public NewCustomerForm(Window containerWindow, CustomerTable customerTable) {
		this.containerWindow = containerWindow;
		this.customerTable = customerTable;
		this.buildForm();
	}

	private void buildForm() {

		FormLayout form = new FormLayout();

		TextField firstNameField = new TextField("First Name");
		firstNameField.setRequiredIndicatorVisible(true);

		form.addComponent(firstNameField);

		TextField lastNameField = new TextField("Last Name");
		form.addComponent(lastNameField);

		TextField phoneNumberField = new TextField("Phone Number");
		form.addComponent(phoneNumberField);

		TextField addressField = new TextField("Address");
		form.addComponent(addressField);

		CustomerDataService customerDataService = new CustomerDataService();

		ArrayList<CustomerStatusDB> customerStatuses = customerDataService.getCustomerStatuses();
		ComboBox<CustomerStatusDB> customerStatusesSelect = new ComboBox<CustomerStatusDB>();
		customerStatusesSelect.setCaption("Customer Status");
		customerStatusesSelect.setItems(customerStatuses);
		customerStatusesSelect.setItemCaptionGenerator(CustomerStatusDB::getStatusName);
		customerStatusesSelect.setSelectedItem(customerStatuses.get(0));

		form.addComponent(customerStatusesSelect);

		new Binder<CustomerDB>().forField(firstNameField)
				.withValidator(str -> str.length() >= 1, "Must be at least 1 char")
				.bind(CustomerDB::getFirstName, CustomerDB::setFirstName);

		this.addComponent(form);

		Button saveCustomerButton = new Button("Save");
		saveCustomerButton.addClickListener(e -> {

			CustomerDB customer = new CustomerDB();
			customer.setFirstName(firstNameField.getValue());
			customer.setLastName(lastNameField.getValue());
			customer.setCustomerPhone(phoneNumberField.getValue());
			customer.setCustomerStatus(customerStatusesSelect.getValue());
			customer.setCustomerAddress(addressField.getValue());
			customerDataService.saveCustomer(customer);
			Notification.show(
					"customer " + customer.getFirstName() + " " + customer.getLastName() + " created successfully");
			
			if (this.customerTable != null) 
				this.customerTable.refreshTable();
			
			containerWindow.setCaption("Customer " + customer.getId());
			containerWindow.setContent(new ViewCustomerForm(customer));

		});

		this.addComponent(saveCustomerButton);

	}

}
