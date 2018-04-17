package org.adyachenko.customers_web;

import java.util.ArrayList;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
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

public class CustomerForm extends VerticalLayout {

	private static final String REQUIRED_FIELD = "Required Field";

	private static final String BUTTON_LABEL_SAVE = "Save";

	private static final String BUTTON_LABEL_EDIT = "Edit";

	private static final long serialVersionUID = 2899390100026188322L;

	private Window containerWindow;
	private CustomerTable customerTable;
	private CustomerDB customer;
	private boolean formEditing = false;

	public CustomerForm(Window containerWindow, CustomerTable customerTable) {
		this.containerWindow = containerWindow;
		this.customerTable = customerTable;
		this.formEditing = true;
		this.buildForm();
	}

	public CustomerForm(Window containerWindow, CustomerTable customerTable, CustomerDB customer) {
		this.containerWindow = containerWindow;
		this.customerTable = customerTable;
		this.customer = customer;
		this.formEditing = false;
		this.buildForm();
	}

	private void buildForm() {

		FormLayout form = new FormLayout();
		Binder<CustomerDB> binder = new Binder<CustomerDB>();

		TextField firstNameField = new TextField("First Name");
		firstNameField.setRequiredIndicatorVisible(true);

		binder.forField(firstNameField).asRequired(REQUIRED_FIELD).bind(CustomerDB::getFirstName,
				CustomerDB::setFirstName);

		form.addComponent(firstNameField);

		TextField lastNameField = new TextField("Last Name");
		binder.forField(lastNameField).asRequired(REQUIRED_FIELD).bind(CustomerDB::getLastName,
				CustomerDB::setLastName);

		form.addComponent(lastNameField);

		TextField phoneNumberField = new TextField("Phone Number");
		binder.forField(phoneNumberField).asRequired(REQUIRED_FIELD).bind(CustomerDB::getCustomerPhone,
				CustomerDB::setCustomerPhone);

		form.addComponent(phoneNumberField);

		TextField addressField = new TextField("Address");
		binder.forField(addressField).asRequired(REQUIRED_FIELD).bind(CustomerDB::getCustomerAddress,
				CustomerDB::setCustomerAddress);
		form.addComponent(addressField);

		CustomerDataService customerDataService = new CustomerDataService();

		ArrayList<CustomerStatusDB> customerStatuses = customerDataService.getCustomerStatuses();
		ComboBox<CustomerStatusDB> customerStatusesSelect = new ComboBox<CustomerStatusDB>();

		customerStatusesSelect.setCaption("Customer Status");
		customerStatusesSelect.setItems(customerStatuses);
		customerStatusesSelect.setItemCaptionGenerator(CustomerStatusDB::getStatusName);
		customerStatusesSelect.setSelectedItem(customerStatuses.get(0));

		binder.forField(customerStatusesSelect).asRequired(REQUIRED_FIELD).bind(CustomerDB::getCustomerStatus,
				CustomerDB::setCustomerStatus);

		if (this.customer != null)
			binder.readBean(this.customer);

		form.addComponent(customerStatusesSelect);

		this.addComponent(form);

		Button saveCustomerButton = new Button();

		if (formEditing) {
			saveCustomerButton.setCaption(BUTTON_LABEL_SAVE);
		} else {
			form.setEnabled(false);
			saveCustomerButton.setCaption(BUTTON_LABEL_EDIT);
		}
		saveCustomerButton.addClickListener(click -> {

			if (formEditing) {

				try {
					if (this.customer == null) {
						this.customer = new CustomerDB();
					}

					binder.writeBean(customer);
					customerDataService.saveCustomer(customer);

					if (this.customerTable != null)
						this.customerTable.refreshTable();

					containerWindow.setCaption("Customer " + customer.getId());
					this.containerWindow.setContent(new CustomerForm(containerWindow, customerTable, customer));
					Notification.show("Customer " + customer.getFirstName() + " " + customer.getLastName()
							+ " saved successfully ");

				} catch (ValidationException e1) {
					// TODO Auto-generated catch block

					Notification.show("Customer could not be saved, please check error messages for each field");
				}
			} else {

				form.setEnabled(true);
				saveCustomerButton.setCaption(BUTTON_LABEL_SAVE);

			}
			formEditing = !formEditing;
		});

		this.addComponent(saveCustomerButton);
		
		if (customer != null) { 
			this.addComponent(new CommentsView(customer));
		}

	}

}
