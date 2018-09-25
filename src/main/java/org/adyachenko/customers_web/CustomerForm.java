package org.adyachenko.customers_web;

import java.util.List;

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
import customers_core.dao.CustomerCoreSessionProvider;
import customers_core.db.CustomerDB;
import customers_core.db.CustomerStatusDB;

public class CustomerForm extends VerticalLayout {

	private static final long serialVersionUID = 2899390100026188322L;

	private Window containerWindow;
	private CustomerTable customerTable;
	private CustomerDB customer;
	private boolean formEditing = false;

	public static CustomerForm getCustomerForm(Window containerWindow, CustomerTable customerTable,
			CustomerDB customer) {
		return new CustomerForm(containerWindow, customerTable, customer);
	}

	public static CustomerForm getNewCustomerForm(Window containerWindow, CustomerTable customerTable) {
		return new CustomerForm(containerWindow, customerTable, null);
	}

	private CustomerForm(Window containerWindow, CustomerTable customerTable, CustomerDB customer) {
		this.containerWindow = containerWindow;
		this.customerTable = customerTable;
		this.customer = customer;
		this.formEditing = false;
		this.buildForm();
	}

	private void buildForm() {

		FormLayout form = new FormLayout();

		Binder<CustomerDB> binder = new Binder<CustomerDB>();

		TextField firstNameField = new TextField(CustomerConstants.LABEL_FIRST_NAME);
		firstNameField.setRequiredIndicatorVisible(true);

		binder.forField(firstNameField).asRequired(CustomerConstants.LABEL_REQUIRED_FIELD)
				.bind(CustomerDB::getFirstName, CustomerDB::setFirstName);

		form.addComponent(firstNameField);

		TextField lastNameField = new TextField(CustomerConstants.LABEL_LAST_NAME);
		binder.forField(lastNameField).asRequired(CustomerConstants.LABEL_REQUIRED_FIELD).bind(CustomerDB::getLastName,
				CustomerDB::setLastName);

		form.addComponent(lastNameField);

		TextField phoneNumberField = new TextField(CustomerConstants.LABEL_PHONE_NUMBER);
		binder.forField(phoneNumberField).asRequired(CustomerConstants.LABEL_REQUIRED_FIELD)
				.bind(CustomerDB::getCustomerPhone, CustomerDB::setCustomerPhone);

		form.addComponent(phoneNumberField);

		TextField addressField = new TextField(CustomerConstants.LABEL_ADDRESS);
		binder.forField(addressField).asRequired(CustomerConstants.LABEL_REQUIRED_FIELD)
				.bind(CustomerDB::getCustomerAddress, CustomerDB::setCustomerAddress);
		form.addComponent(addressField);

		CustomerDataService customerDataService = new CustomerDataService(new CustomerCoreSessionProvider());

		List<CustomerStatusDB> customerStatuses = customerDataService.getCustomerStatuses();
		ComboBox<CustomerStatusDB> customerStatusesSelect = new ComboBox<CustomerStatusDB>();

		customerStatusesSelect.setCaption(CustomerConstants.CUSTOMER_STATUS);
		customerStatusesSelect.setItems(customerStatuses);
		customerStatusesSelect.setItemCaptionGenerator(CustomerStatusDB::getStatusName);
		customerStatusesSelect.setSelectedItem(customerStatuses.get(0));

		binder.forField(customerStatusesSelect).asRequired(CustomerConstants.LABEL_REQUIRED_FIELD)
				.bind(CustomerDB::getCustomerStatus, CustomerDB::setCustomerStatus);

		if (this.customer != null)
			binder.readBean(this.customer);

		form.addComponent(customerStatusesSelect);

		this.addComponent(form);

		Button saveCustomerButton = new Button();

		if (formEditing) {
			saveCustomerButton.setCaption(CustomerConstants.LABEL_SAVE);
		} else {
			form.setEnabled(false);
			saveCustomerButton.setCaption(CustomerConstants.LABEL_EDIT);
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
					this.containerWindow.setContent(getCustomerForm(containerWindow, customerTable, customer));
					Notification.show("Customer " + customer.getFirstName() + " " + customer.getLastName()
							+ " saved successfully ");

				} catch (ValidationException e1) {
					// TODO Auto-generated catch block

					Notification.show("Customer could not be saved, please check error messages for each field");
				}
			} else {

				form.setEnabled(true);
				saveCustomerButton.setCaption(CustomerConstants.LABEL_SAVE);

			}
			formEditing = !formEditing;
		});

		this.addComponent(saveCustomerButton);

		if (customer != null) {
			this.addComponent(new CommentsView(customer));
		}

	}

}
