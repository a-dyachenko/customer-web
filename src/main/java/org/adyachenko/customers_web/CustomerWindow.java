package org.adyachenko.customers_web;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import customers_core.db.CustomerDB;

public class CustomerWindow extends Window {

	private static final long serialVersionUID = 7778169841207676296L;

	private static final String STYLE_CUSTOMERS_VIEW = "customer-view";
	private static final String CAPTION_CUSTOMER = "Customer ";
	private static CustomerWindow customerWindow;

	public static void popupNewCustomerWindow(CustomerTable customerTable) {
		popupCustomerWindow(customerTable, null);
	}

	public static void popupCustomerWindow(CustomerTable customerTable, CustomerDB customer) {

		if (customerWindow == null) {

			customerWindow = new CustomerWindow();
			customerWindow.setCaption(CAPTION_CUSTOMER + customer.getFirstName() + " " + customer.getLastName());
			customerWindow.center();
			customerWindow.setPositionY(50);
			customerWindow.setPositionX(250);
			customerWindow.addStyleName(STYLE_CUSTOMERS_VIEW);

		}

		CustomerForm customerForm = CustomerForm.getCustomerForm(customerWindow, customerTable, customer);
		customerWindow.setContent(customerForm); 
		if (customerWindow.getParent() == null)
			UI.getCurrent().addWindow(customerWindow);

	}

}
