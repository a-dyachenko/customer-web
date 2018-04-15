package org.adyachenko.customers_web;

import java.util.ArrayList;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import customer_core.service.CustomerDataService;
import customers_core.db.CustomerDB;
import customers_core.db.CustomerStatusDB;

public class ViewCustomerForm extends VerticalLayout {

	private static final String BUTTON_LABEL_SAVE = "Save";

	private static final String BUTTON_LABEL_EDIT = "Edit";

	private static final long serialVersionUID = -1850057846022780831L;

	private boolean formEditing = false;
	private CustomerDB customer;
	private CustomerTable customerTable;

	public ViewCustomerForm(CustomerDB customer, CustomerTable customerTable) {
		this.customer = customer;
		this.customerTable = customerTable;
		this.build();
	}

	public ViewCustomerForm(CustomerDB customer) {
		this.customer = customer;
		this.build();

	}

	private void build() {
		FormLayout form = new FormLayout();

		form.setEnabled(false);

		TextField firstNameField = new TextField("First Name");
		String firstName = customer.getFirstName();
		if (firstName != null)
			firstNameField.setValue(firstName);
		form.addComponent(firstNameField);

		TextField lastNameField = new TextField("Last Name");
		String lastName = customer.getLastName();
		if (lastName != null)
			lastNameField.setValue(lastName);
		form.addComponent(lastNameField);

		TextField phoneNumberField = new TextField("Phone Number");
		String customerPhone = customer.getCustomerPhone();
		if (customerPhone != null)
			phoneNumberField.setValue(customerPhone);
		form.addComponent(phoneNumberField);

		TextField addressField = new TextField("Address");
		String customerAddress = customer.getCustomerAddress();
		if (customerAddress != null)
			addressField.setValue(customerAddress);
		form.addComponent(addressField);

		CustomerDataService customerDataService = new CustomerDataService();

		ArrayList<CustomerStatusDB> customerStatuses = customerDataService.getCustomerStatuses();
		ComboBox<CustomerStatusDB> customerStatusesSelect = new ComboBox<CustomerStatusDB>();
		customerStatusesSelect.setCaption("Customer Status");
		customerStatusesSelect.setItems(customerStatuses);
		customerStatusesSelect.setItemCaptionGenerator(CustomerStatusDB::getStatusName);
		customerStatusesSelect.setSelectedItem(customer.getCustomerStatus());

		form.addComponent(customerStatusesSelect);

		this.addComponent(form);

		Button saveCustomerButton = new Button(BUTTON_LABEL_EDIT);

		saveCustomerButton.addClickListener(e -> {
			if (!formEditing) {
				saveCustomerButton.setCaption(BUTTON_LABEL_SAVE);
				form.setEnabled(true);
				formEditing = true;
			} else {
				customer.setFirstName(firstNameField.getValue());
				customer.setLastName(lastNameField.getValue());
				customer.setCustomerPhone(phoneNumberField.getValue());
				customer.setCustomerStatus(customerStatusesSelect.getValue());
				customer.setCustomerAddress(addressField.getValue());
				customerDataService.saveCustomer(customer);
				form.setEnabled(false);
				saveCustomerButton.setCaption(BUTTON_LABEL_EDIT);
				formEditing = false;
				Notification.show("customer " + firstName + " " + lastName + " updated successfully");
				
				if (customerTable != null)
					customerTable.refreshTable();
			}
		});

		this.addComponent(saveCustomerButton);
		this.addComponent(new CommentsView(customer));

	}
}
