package org.adyachenko.customers_web;

import java.util.ArrayList;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import customer_core.service.CustomerDataService;
import customers_core.db.CommentDB;
import customers_core.db.CustomerDB;

public class CommentsView extends VerticalLayout {

	CustomerDB customer;
	VerticalLayout commentsLayout;
	private static final long serialVersionUID = 5282568066585928348L;

	public CommentsView(CustomerDB customer) { 
		this.addStyleName("customers-comment-view");
		commentsLayout = new VerticalLayout();
		commentsLayout.addStyleName("customers-comment-layout");
		this.customer = customer;
		this.build();
	}

	private void build() {

		Label commentLabel = new Label("Customer Comments");
		this.addComponent(commentLabel);
		TextArea commentArea = new TextArea("Comment text");
		this.addComponent(commentArea);
		Button submitButton = new Button("Submit comment");

		CustomerDataService customerDataService = new CustomerDataService();
		submitButton.addClickListener(click -> {
			CommentDB comment = new CommentDB();
			comment.setCustomer(customer);
			comment.setCommentText(commentArea.getValue());
			customerDataService.saveComment(comment);
			refreshComments(customerDataService);
		});

		this.addComponent(submitButton); 
		this.addComponent(commentsLayout);
		
		refreshComments(customerDataService);

	}

	private void refreshComments(CustomerDataService customerDataService) {
		
		this.commentsLayout.removeAllComponents();
		ArrayList<CommentDB> customerComments = customerDataService.getCommentsForCustomer(customer);

		if (customerComments != null && !customerComments.isEmpty()) {
			 
			for (CommentDB comment : customerComments) {
				
				Panel commentWrapper = new Panel();
				commentWrapper.setContent(new Label(comment.getCommentText(), ContentMode.PREFORMATTED)); 
				commentsLayout.addComponent(commentWrapper);
			}
		}
	}

}
