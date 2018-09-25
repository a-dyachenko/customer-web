package org.adyachenko.customers_web;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import customers_core.db.CustomerDB;

public class CustomerWindow extends Window {

	private static final long serialVersionUID = 7778169841207676296L;

	private static final String STYLE_CUSTOMERS_VIEW = "customer-view";

	private static final String CAPTION_CUSTOMER = "Customer ";
	private static final String CAPTION_NEW_CUSTOMER = "New Customer";

	public static void popupNewCustomerWindow(CustomerTable customerTable) {

		CustomerWindow customerWindow = buildPopupWindow();

		CustomerForm customerForm = CustomerForm.getNewCustomerForm(customerWindow, customerTable);
		customerWindow.setContent(customerForm);

		String existingCustomerCaption = CAPTION_NEW_CUSTOMER;

		customerWindow.setCaption(existingCustomerCaption);

		UI.getCurrent().addWindow(customerWindow);

	}

	public static void popupCustomerWindow(CustomerTable customerTable, CustomerDB customer) {

		CustomerWindow customerWindow = buildPopupWindow();

		CustomerForm customerForm = CustomerForm.getCustomerForm(customerWindow, customerTable, customer);
		customerWindow.setContent(customerForm);

		String existingCustomerCaption = CAPTION_CUSTOMER + customer.getFirstName() + " " + customer.getLastName();

		customerWindow.setCaption(existingCustomerCaption);

		UI.getCurrent().addWindow(customerWindow);

	}

	private static CustomerWindow buildPopupWindow() {

		CustomerWindow customerWindow = new CustomerWindow();

		customerWindow.center();
		customerWindow.setPositionY(50);
		customerWindow.setPositionX(250);
		customerWindow.setWidth("30%");
		customerWindow.setHeight("60%");
		customerWindow.addStyleName(STYLE_CUSTOMERS_VIEW);

		return customerWindow;
	}

}
