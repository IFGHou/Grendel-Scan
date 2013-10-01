package com.grendelscan.GUI2.proxy.interception;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GGroup;

import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.scan.Scan;

public class InterceptionSettingsComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements GrendelSettingsControl{


	private GGroup	requestInterceptionGroup;
	private GButton	enableInterceptRequestCheck;
	private InterceptionRulesComposite	responseInterceptFilters;
	private InterceptionRulesComposite	requestInterceptFilters;
	private GGroup	responseInterceptionGroup;
	private GButton	enableInterceptResponseCheck;
	

	public InterceptionSettingsComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			
			{
				requestInterceptionGroup = new GGroup(this, SWT.NONE);
				FormLayout requestInterceptionGroupLayout = new FormLayout();
				requestInterceptionGroup.setLayout(requestInterceptionGroupLayout);
				FormData requestInterceptionGroupLData = new FormData();
				requestInterceptionGroupLData.width = 791;
				requestInterceptionGroupLData.height = 214;
				requestInterceptionGroupLData.left =  new FormAttachment(0, 1000, 5);
				requestInterceptionGroupLData.top =  new FormAttachment(0, 1000, 35);
				requestInterceptionGroupLData.right =  new FormAttachment(1000, 1000, -5);
				requestInterceptionGroupLData.bottom =  new FormAttachment(530, 1000, 0);
				requestInterceptionGroup.setLayoutData(requestInterceptionGroupLData);
				requestInterceptionGroup.setText("Request Interception");
				{
					enableInterceptRequestCheck = new GButton(requestInterceptionGroup, SWT.CHECK | SWT.LEFT);
					FormData enableRequestCheckLData = new FormData();
					enableRequestCheckLData.width = 198;
					enableRequestCheckLData.height = 19;
					enableRequestCheckLData.left =  new FormAttachment(0, 1000, 5);
					enableRequestCheckLData.top =  new FormAttachment(0, 1000, 5);
					enableInterceptRequestCheck.setLayoutData(enableRequestCheckLData);
					enableInterceptRequestCheck.setText("Enable request interception");
					enableInterceptRequestCheck.addSelectionListener(new SelectionAdapter() {
						@Override
                        public void widgetSelected(SelectionEvent evt) 
						{
							updateInterceptRequestComposite(enableInterceptRequestCheck.getSelection());
						}
					});
				}
				{
					FormData requestInterceptFiltersLData = new FormData();
					requestInterceptFiltersLData.width = 767;
					requestInterceptFiltersLData.height = 145;
					requestInterceptFiltersLData.left =  new FormAttachment(0, 1000, 0);
					requestInterceptFiltersLData.top =  new FormAttachment(0, 1000, 30);
					requestInterceptFiltersLData.right =  new FormAttachment(1000, 1000, 0);
					requestInterceptFiltersLData.bottom =  new FormAttachment(1000, 1000, 0);
					FilterChangeHandler changeHandler = new FilterChangeHandler()
					{
						@Override
						public void addFilter(InterceptFilter filter)
						{
							Scan.getScanSettings().addRequestInterceptFilter(filter);
						}
						@Override
						public void removeFilter(InterceptFilter filter)
						{
							Scan.getScanSettings().removeRequestInterceptFilter(filter);
						}
					};
					requestInterceptFilters = new InterceptionRulesComposite(
							requestInterceptionGroup, SWT.NONE, InterceptFilterLocation.getRequestLocations(), false, changeHandler); 
					requestInterceptFilters.setLayoutData(requestInterceptFiltersLData);
					requestInterceptFilters.setEnabled(false);
					
				}
			}
			{
				responseInterceptionGroup = new GGroup(this, SWT.NONE);
				FormLayout requestInterceptionGroupLayout = new FormLayout();
				responseInterceptionGroup.setLayout(requestInterceptionGroupLayout);
				FormData responseInterceptionGroupLData = new FormData();
				responseInterceptionGroupLData.width = 805;
				responseInterceptionGroupLData.height = 214;
				responseInterceptionGroupLData.left =  new FormAttachment(0, 1000, 5);
				responseInterceptionGroupLData.top =  new FormAttachment(540, 1000, 0);
				responseInterceptionGroupLData.right =  new FormAttachment(1000, 1000, -5);
				responseInterceptionGroupLData.bottom =  new FormAttachment(1000, 1000, -5);
				responseInterceptionGroup.setLayoutData(responseInterceptionGroupLData);
				responseInterceptionGroup.setText("Response Interception");
				{
					enableInterceptResponseCheck = new GButton(responseInterceptionGroup, SWT.CHECK | SWT.LEFT);
					FormData enableResponseCheckLData = new FormData();
					enableResponseCheckLData.width = 215;
					enableResponseCheckLData.height = 19;
					enableResponseCheckLData.left =  new FormAttachment(0, 1000, 5);
					enableResponseCheckLData.top =  new FormAttachment(0, 1000, 5);
					enableInterceptResponseCheck.setLayoutData(enableResponseCheckLData);
					enableInterceptResponseCheck.setText("Enable response interception");
					enableInterceptResponseCheck.addSelectionListener(new SelectionAdapter() {
						@Override
                        public void widgetSelected(SelectionEvent evt) 
						{
							updateInterceptResponseComposite(enableInterceptResponseCheck.getSelection());
						}
					});
				}
				{
					FormData responseInterceptFiltersLData = new FormData();
					responseInterceptFiltersLData.width = 767;
					responseInterceptFiltersLData.height = 145;
					responseInterceptFiltersLData.left =  new FormAttachment(0, 1000, 0);
					responseInterceptFiltersLData.top =  new FormAttachment(0, 1000, 30);
					responseInterceptFiltersLData.right =  new FormAttachment(1000, 1000, 0);
					responseInterceptFiltersLData.bottom =  new FormAttachment(1000, 1000, 0);
					FilterChangeHandler changeHandler = new FilterChangeHandler()
					{
						@Override
						public void addFilter(InterceptFilter filter)
						{
							Scan.getScanSettings().addResponseInterceptFilter(filter);
						}
						@Override
						public void removeFilter(InterceptFilter filter)
						{
							Scan.getScanSettings().removeResponseInterceptFilter(filter);
						}
					};
					responseInterceptFilters = new InterceptionRulesComposite(
							responseInterceptionGroup, SWT.NONE, InterceptFilterLocation.getAllLocations(), false, changeHandler); 
					responseInterceptFilters.setLayoutData(responseInterceptFiltersLData);
					responseInterceptFilters.setEnabled(false);
				}
			}
			this.layout();
	}

	protected void updateInterceptRequestComposite(boolean enabled)
	{
		Scan.getScanSettings().setInterceptRequests(enabled);
		requestInterceptFilters.setEnabled(enabled);
	}

	protected void updateInterceptResponseComposite(boolean enabled)
	{
		Scan.getScanSettings().setInterceptResponses(enabled);
		responseInterceptFilters.setEnabled(enabled);
	}

	@Override
	public void updateFromSettings()
	{
//		requestInterceptFilters.addStandardFilter(InterceptFilterLocation.METHOD, "GET|POST", true, true);
//		requestInterceptFilters.addStandardFilter(InterceptFilterLocation.PATH, 
//				"\\.(?:bmp|jpe?g|gif|tiff?|png|swf|mpe?g|mov|avi|wmv|mp3|wma|wav|docx?|xlsx?|mdb|rtf|pptx?|pdf)$", true, false);
//		requestInterceptFilters.addStandardFilter(InterceptFilterLocation.PATH, "\\.(?:css|js|vbs)$", true, false);

//		responseInterceptFilters.addStandardFilter(InterceptFilterLocation.RESPONSE_MIME_TYPE, "text/.*", true, true);

		enableInterceptRequestCheck.setSelection(Scan.getScanSettings().isInterceptRequests());
		enableInterceptResponseCheck.setSelection(Scan.getScanSettings().isInterceptResponses());
		requestInterceptFilters.updateFilterList(Scan.getScanSettings().getReadOnlyRequestInterceptFilters());
		responseInterceptFilters.updateFilterList(Scan.getScanSettings().getReadOnlyResponseInterceptFilters());
	}
	
	public void changeEnableInterceptResponseCheck(boolean enabled)
    {
		enableInterceptResponseCheck.setSelection(enabled);
		updateInterceptResponseComposite(enabled);
    }
	
	public void changeEnableInterceptRequestCheck(boolean enabled)
    {
		updateInterceptRequestComposite(enabled);
		enableInterceptRequestCheck.setSelection(enabled);
    }

	public final InterceptionRulesComposite getResponseInterceptFilters()
	{
		return responseInterceptFilters;
	}

	public final InterceptionRulesComposite getRequestInterceptFilters()
	{
		return requestInterceptFilters;
	}

	@Override
	public String updateToSettings()
	{
		// Everything updates ScanSettings on their own
		return "";
	}

}
