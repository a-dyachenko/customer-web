package org.adyachenko.customers_web;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class CustomersUI extends UI {

	private static final long serialVersionUID = 6407949651043622490L;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		final VerticalLayout layout = new VerticalLayout();
		Label caption = new Label("Customer Database Application");
		caption.addStyleName("customer-ui-label");
		layout.addComponent(caption);

		CustomerTable customerTable = new CustomerTable();

		layout.addComponent(customerTable);
		Button newCustomerButton = new Button("Add New Customer");
		newCustomerButton.addClickListener(click -> {

			if (getWindows().size() == 0) {

				Window popupWindow = new Window("Create Customer");
				popupWindow.setContent(new CustomerForm(popupWindow, customerTable));
				popupWindow.setWidth("700");
				popupWindow.center();
				popupWindow.setPositionY(50);
				popupWindow.setPositionX(250);
				popupWindow.setResizable(false);
				addWindow(popupWindow);
			}

		});

		layout.addComponent(newCustomerButton);

		setContent(layout);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = CustomersUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 8610851045526217362L;
	}

}
