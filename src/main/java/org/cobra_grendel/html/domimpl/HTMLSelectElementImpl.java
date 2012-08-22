package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.html.FormInput;
import org.w3c.dom.DOMException;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLOptionsCollection;
import org.w3c.dom.html2.HTMLSelectElement;

import java.util.*;
import org.mozilla.javascript.*;

public class HTMLSelectElementImpl extends HTMLBaseInputElement implements
		HTMLSelectElement {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLSelectElementImpl(String name, int transactionId) {
		super(name, transactionId);
	}

	@Override public void add(HTMLElement element, HTMLElement before)
			throws DOMException {
		this.insertBefore(element, before);
	}

	@Override public int getLength() {
		return this.getOptions().getLength();
	}

	private Boolean multipleState = null;
	
	@Override public boolean getMultiple() {
		Boolean m = this.multipleState;
		if(m != null) {
			return m.booleanValue();
		}
		return this.getAttributeAsBoolean("multiple");
	}

	private HTMLOptionsCollection options;
	
	@Override public HTMLOptionsCollection getOptions() {
		synchronized(this) {
			if(this.options == null) {
				this.options = new HTMLOptionsCollectionImpl(this, transactionId);
			}
			return this.options;
		}
	}

	@Override public int getSelectedIndex() {
		InputContext ic = this.inputContext;
		if(ic != null) {
			return ic.getSelectedIndex();
		}
		else {
			return this.deferredSelectedIndex;
		}
	}

	@Override public int getSize() {
		InputContext ic = this.inputContext;
		if(ic != null) {
			return ic.getVisibleSize();
		}
		else {
			return 0;
		}
	}

	@Override public String getType() {
		return this.getMultiple() ? "select-multiple" : "select-one";
	}

	@Override public void remove(int index) {
		try {
			this.removeChild(this.getOptions().item(index));
		} catch(DOMException de) {
			this.warn("remove(): Unable to remove option at index " + index + ".", de);
		}
	}

	@Override public void setLength(int length) throws DOMException {
		this.getOptions().setLength(length);
	}

	@Override public void setMultiple(boolean multiple) {
		boolean prevMultiple = this.getMultiple();
		this.multipleState = Boolean.valueOf(multiple);
		if(prevMultiple != multiple) {
			this.informLayoutInvalid();
		}
	}

	private int deferredSelectedIndex = -1;
	
	@Override public void setSelectedIndex(int selectedIndex) {
		this.setSelectedIndexImpl(selectedIndex);
		HTMLOptionsCollection options = this.getOptions();
		int length = options.getLength();
		for(int i = 0; i < length; i++) {
			HTMLOptionElementImpl option = (HTMLOptionElementImpl) options.item(i);
			option.setSelectedImpl(i == selectedIndex);
		}
	}

	void setSelectedIndexImpl(int selectedIndex) {
		InputContext ic = this.inputContext;
		if(ic != null) {
			ic.setSelectedIndex(selectedIndex);
		}
		else {
			this.deferredSelectedIndex = selectedIndex;
		}
	}

	@Override public void setSize(int size) {
		InputContext ic = this.inputContext;
		if(ic != null) {
			ic.setVisibleSize(size);
		}
	}

	@Override public FormInput[] getFormInputs() {
		getOptions(); // to initialize if needed
		ArrayList<FormInput> formInputs = new ArrayList<FormInput>();

		if (getValue() != null)
		{
			formInputs.add(new FormInput(getName(), getValue()));
		}
		
		for (int i = 0; i < options.getLength(); i++)
		{
			HTMLOptionElementImpl option = (HTMLOptionElementImpl) options.item(i);
			formInputs.add(new FormInput(getName(), option.getValue()));
		}

		return formInputs.toArray(FormInput.EMPTY_ARRAY);
	}	
	
	@Override public void resetInput() {
		InputContext ic = this.inputContext;
		if(ic != null) {
			ic.resetInput();
		}
	}

	@Override public void setInputContext(InputContext ic) {
		super.setInputContext(ic);
		if(ic != null) {
			ic.setSelectedIndex(this.deferredSelectedIndex);
		}
	}
	
	private Function onchange;
	
	public Function getOnchange() {
		return this.getEventFunction(this.onchange, "onchange");
	}
	
	public void setOnchange(Function value) {
		this.onchange = value;
	}
}