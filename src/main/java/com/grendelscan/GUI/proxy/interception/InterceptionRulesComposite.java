package com.grendelscan.GUI.proxy.interception;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class InterceptionRulesComposite extends ScrolledComposite
{

	protected TableColumn				actionColumn;
	protected Combo						actionCombo;
	protected Button					addButton;
	protected TableColumn				componentColumn;
	protected Combo						componentCombo;
	protected Table						criteriaTable;
	protected final String				DOESNT_MATCH	= "doesn't match";
	protected final String				DONT_INTERCEPT;
	protected Label						ifLabel;
	protected final String				INTERCEPT;
	protected final String				MATCHES			= "matches";
	protected Combo						matchesCombo;
	protected Text						patternText;
	protected TableColumn				relationColumn;
	protected Button					removeButton;

	protected Button					resetButton;
	protected Label						thenLabel;
	protected Label						thresholdLabel;
	protected Text						thresholdText;
	protected Label						transactionNumberLabel;
	protected Text						transactionNumberText;

	protected TableColumn				valueColumn;
	final protected FilterChangeHandler changeHandler;

	public void updateFilterList(final List<InterceptFilter> newFilterList)
	{
		criteriaTable.clearAll();
		for (InterceptFilter filter : newFilterList)
		{
			TableItem item = new TableItem(criteriaTable, SWT.NONE);
			item.setText(new String[] { filter.getLocation().getText(),
					filter.isMatches() ? MATCHES : DOESNT_MATCH,
					filter.getDisplayText(),
					filter.isIntercept() ? INTERCEPT : DONT_INTERCEPT });
			item.setData(filter);
		}
	}
	
	
	public InterceptionRulesComposite(org.eclipse.swt.widgets.Composite parent, int style,
			InterceptFilterLocation[] locations, boolean fuzzing, FilterChangeHandler changeHandler)
	{
		super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		this.changeHandler = changeHandler; 
		if (fuzzing)
		{
			INTERCEPT = "is a match";
			DONT_INTERCEPT = "isn't a match";
		}
		else
		{
			INTERCEPT = "intercept";
			DONT_INTERCEPT = "don't intercept";
		}
		initGUI(locations);
	}
	

	public void addFuzzyResponseFilter(StandardHttpTransaction transaction, int threshold, boolean matches,
			boolean intercept)
	{
		FuzzyResponseFilter filter = new FuzzyResponseFilter(matches, intercept, threshold, transaction);

		TableItem item = new TableItem(criteriaTable, SWT.NONE);
		item.setText(new String[] { InterceptFilterLocation.FUZZY_RESPONSE_COMPARE.getText(),
					matches ? MATCHES : DOESNT_MATCH, filter.getDisplayText(),
					intercept ? INTERCEPT : DONT_INTERCEPT });
		item.setData(filter);
		changeHandler.addFilter(filter);
	}

	
	public void addStandardFilter(InterceptFilterLocation location, String patternString, boolean matches,
			boolean intercept)
	{
		TableItem item = new TableItem(criteriaTable, SWT.NONE);
		item.setText(new String[] { location.getText(), matches ? MATCHES : DOESNT_MATCH,
				patternString, intercept ? INTERCEPT : DONT_INTERCEPT });
		StandardInterceptFilter filter = new StandardInterceptFilter(location, patternString, matches, intercept);
		item.setData(filter);
		changeHandler.addFilter(filter);
	}

	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		componentCombo.setEnabled(enabled);
		matchesCombo.setEnabled(enabled);
		resetButton.setEnabled(enabled);
		addButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		criteriaTable.setEnabled(enabled);
		thenLabel.setEnabled(enabled);
		actionCombo.setEnabled(enabled);
		patternText.setEnabled(enabled);
		ifLabel.setEnabled(enabled);
	}

	private void initGUI(InterceptFilterLocation[] locations)
	{
			setLayout(new FillLayout(org.eclipse.swt.SWT.HORIZONTAL));
			setExpandHorizontal(true);
			setExpandVertical(true);
			EnforceIntegersOnly numbersOnlyVerifyer = new EnforceIntegersOnly();
			// addFocusListener(new FocusListener()
			// {
			// public void focusGained(FocusEvent event)
			// {
			// transactionNumberCombo.setItems(items)
			// scan.getTransactionRecord().getAllCompletedTransactionIDs();
			// }
			//
			// public void focusLost(FocusEvent arg0)
			// {
			// // don't need anything here
			// }
			//
			// });

			Composite mainComposite = new Composite(this, SWT.NONE);
			mainComposite.setLayout(new FormLayout());
			setContent(mainComposite);
			{
				ifLabel = new Label(mainComposite, SWT.NONE);
				FormData ifLabelLData = new FormData();
				ifLabelLData.width = 8;
				ifLabelLData.height = 25;
				ifLabelLData.left = new FormAttachment(0, 1000, 5);
				ifLabelLData.top = new FormAttachment(0, 1000, 7);
				ifLabel.setLayoutData(ifLabelLData);
				ifLabel.setText("If");
			}
			{
				FormData componentComboLData = new FormData();
				componentComboLData.width = 151;
				componentComboLData.height = 25;
				componentComboLData.left = new FormAttachment(0, 1000, 19);
				componentComboLData.top = new FormAttachment(0, 1000, 5);
				componentCombo = new Combo(mainComposite, SWT.READ_ONLY);
				componentCombo.setLayoutData(componentComboLData);
				for (InterceptFilterLocation location : locations)
				{
					componentCombo.add(location.getText());
					componentCombo.setData(location.getText(), location);
				}
				componentCombo.setText(componentCombo.getItem(0));
				componentCombo.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(ModifyEvent event)
					{
						if (componentCombo.getText().equals(InterceptFilterLocation.FUZZY_RESPONSE_COMPARE.getText()))
						{
							fuzzyVisibility(true);
						}
						else
						{
							fuzzyVisibility(false);
						}
					}
				});
			}
			{
				FormData matchesComboLData = new FormData();
				matchesComboLData.width = 102;
				matchesComboLData.height = 25;
				matchesComboLData.left = new FormAttachment(0, 1000, 213);
				matchesComboLData.top = new FormAttachment(0, 1000, 5);
				matchesCombo = new Combo(mainComposite, SWT.READ_ONLY);

				matchesCombo.setLayoutData(matchesComboLData);
				matchesCombo.setItems(new String[] { MATCHES, DOESNT_MATCH });
				matchesCombo.setText(matchesCombo.getItem(0));
			}
			{
				FormData patternTextLData = new FormData();
				patternTextLData.width = 236;
				patternTextLData.height = 19;
				patternTextLData.left = new FormAttachment(0, 1000, 359);
				patternTextLData.top = new FormAttachment(0, 1000, 5);
				patternTextLData.right = new FormAttachment(1000, 1000, -179);
				patternText = new Text(mainComposite, SWT.BORDER);
				patternText.setLayoutData(patternTextLData);
			}
			{
				{
					FormData transactionNumberLabelLData = new FormData();
					transactionNumberLabelLData.width = 80;
					transactionNumberLabelLData.height = 19;
					transactionNumberLabelLData.left = new FormAttachment(0, 1000, 359);
					transactionNumberLabelLData.top = new FormAttachment(0, 1000, 7);
					transactionNumberLabel = new Label(mainComposite, SWT.NONE);
					transactionNumberLabel.setText("Transaction: ");
					transactionNumberLabel.setLayoutData(transactionNumberLabelLData);
				}
				{
					FormData transactionNumberComboLData = new FormData();
					transactionNumberComboLData.width = 50;
					transactionNumberComboLData.height = 19;
					transactionNumberComboLData.left = new FormAttachment(0, 1000, 440);
					transactionNumberComboLData.top = new FormAttachment(0, 1000, 5);
					transactionNumberText = new Text(mainComposite, SWT.BORDER);
					transactionNumberText.setLayoutData(transactionNumberComboLData);
					transactionNumberText.addVerifyListener(numbersOnlyVerifyer);
				}
				{
					FormData thresholdLabelLData = new FormData();
					thresholdLabelLData.width = 120;
					thresholdLabelLData.height = 19;
					thresholdLabelLData.left = new FormAttachment(0, 1000, 500);
					thresholdLabelLData.top = new FormAttachment(0, 1000, 7);
					thresholdLabel = new Label(mainComposite, SWT.NONE);
					thresholdLabel.setText("Threshold (0-99):");
					thresholdLabel.setLayoutData(thresholdLabelLData);
				}
				{
					FormData thresholdTextLData = new FormData();
					thresholdTextLData.width = 30;
					thresholdTextLData.height = 19;
					thresholdTextLData.left = new FormAttachment(0, 1000, 625);
					thresholdTextLData.top = new FormAttachment(0, 1000, 5);
					thresholdText = new Text(mainComposite, SWT.BORDER);
					thresholdText.setLayoutData(thresholdTextLData);
					thresholdText.addVerifyListener(numbersOnlyVerifyer);
					thresholdText.setTextLimit(2);
				}
			}
			{
				thenLabel = new Label(mainComposite, SWT.NONE);
				FormData thenLabelLData = new FormData();
				thenLabelLData.width = 33;
				thenLabelLData.height = 25;
				thenLabelLData.top = new FormAttachment(0, 1000, 7);
				thenLabelLData.right = new FormAttachment(1000, 1000, -140);
				thenLabel.setLayoutData(thenLabelLData);
				thenLabel.setText("then");
			}
			{
				FormData actionComboLData = new FormData();
				actionComboLData.width = 93;
				actionComboLData.height = 25;
				actionComboLData.top = new FormAttachment(0, 1000, 5);
				actionComboLData.right = new FormAttachment(1000, 1000, -5);
				actionCombo = new Combo(mainComposite, SWT.READ_ONLY);
				actionCombo.setLayoutData(actionComboLData);
				actionCombo.setItems(new String[] { INTERCEPT, DONT_INTERCEPT });
				actionCombo.setText(actionCombo.getItem(0));
			}
			{
				resetButton = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
				FormData resetButtonLData = new FormData();
				resetButtonLData.width = 60;
				resetButtonLData.height = 27;
				resetButtonLData.top = new FormAttachment(0, 1000, 42);
				resetButtonLData.right = new FormAttachment(1000, 1000, -172);
				resetButton.setLayoutData(resetButtonLData);
				resetButton.setText("Reset");
				resetButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent evt)
					{
						resetButtonWidgetSelected(evt);
					}
				});
			}
			{
				addButton = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
				FormData addButtonLData = new FormData();
				addButtonLData.width = 68;
				addButtonLData.height = 27;
				addButtonLData.top = new FormAttachment(0, 1000, 42);
				addButtonLData.right = new FormAttachment(1000, 1000, -92);
				addButton.setLayoutData(addButtonLData);
				addButton.setText("Add");
				addButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent evt)
					{
						addButtonWidgetSelected(evt);
					}
				});
			}
			{
				removeButton = new Button(mainComposite, SWT.PUSH | SWT.CENTER);
				FormData removeButtonLData = new FormData();
				removeButtonLData.width = 75;
				removeButtonLData.height = 27;
				removeButtonLData.top = new FormAttachment(0, 1000, 42);
				removeButtonLData.right = new FormAttachment(1000, 1000, -5);
				removeButton.setLayoutData(removeButtonLData);
				removeButton.setText("Remove");
				removeButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent evt)
					{
						removeButtonWidgetSelected(evt);
					}
				});
			}
			{
				FormData criteriaTableLData = new FormData();
				criteriaTableLData.width = 737;
				criteriaTableLData.height = 187;
				criteriaTableLData.left = new FormAttachment(0, 1000, 7);
				criteriaTableLData.top = new FormAttachment(0, 1000, 81);
				criteriaTableLData.bottom = new FormAttachment(1000, 1000, -5);
				criteriaTableLData.right = new FormAttachment(1000, 1000, -5);
				criteriaTable = new Table(mainComposite, SWT.FULL_SELECTION | SWT.BORDER);
				criteriaTable.setLayoutData(criteriaTableLData);
				criteriaTable.setHeaderVisible(true);
				{
					componentColumn = new TableColumn(criteriaTable, SWT.NONE);
					componentColumn.setText("Component");
					componentColumn.setWidth(170);
				}
				{
					relationColumn = new TableColumn(criteriaTable, SWT.NONE);
					relationColumn.setText("Relation");
					relationColumn.setWidth(100);
				}
				{
					valueColumn = new TableColumn(criteriaTable, SWT.NONE);
					valueColumn.setText("Value");
					valueColumn.setWidth(370);
				}
				{
					actionColumn = new TableColumn(criteriaTable, SWT.NONE);
					actionColumn.setText("Action");
					actionColumn.setWidth(120);
				}
			}
			fuzzyVisibility(false);
			this.layout();
	}

	protected void addButtonWidgetSelected(SelectionEvent evt)
	{
		boolean good = true;
		boolean fuzzy = thresholdLabel.getVisible();
		boolean matches = matchesCombo.getText().equals(MATCHES) ? true : false;
		boolean intercept = actionCombo.getText().equals(INTERCEPT) ? true : false;
		StandardHttpTransaction transaction = null;
		int transactionID = 0;
		if (!transactionNumberText.getText().equals(""))
		{
			transactionID = Integer.valueOf(transactionNumberText.getText());
		}
		int threshold = 0;
		if (!thresholdText.getText().equals(""))
		{
			threshold = Integer.valueOf(thresholdText.getText());
		}
		String messageText = "";
		if (!fuzzy)
		{
			String pattern = patternText.getText();
			try
			{
				Pattern.compile(pattern);
			}
			catch (PatternSyntaxException e)
			{
				good = false;
				messageText = "Invalid regular expression format: " + e.toString();
			}
			if (patternText.getText().equals(""))
			{
				messageText += "This argument will match all transactions.\n";
			}
		}
		else
		{
			transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
			if (transaction == null)
			{
				good = false;
				messageText += "You didn't supply a valid transaction ID.\n";
			}
			else if (!transaction.isSuccessfullExecution())
			{
				good = false;
				messageText += "The transaction you referenced doesn't have a response.\n";
			}
			if (threshold < 50)
			{
				messageText += "The threshold is low, likely resulting in many matches.\n";
			}
		}

		if (!messageText.equals(""))
		{
			MessageBox m = new MessageBox(getShell(), SWT.OK);
			m.setMessage(messageText);
			m.setText("Warning");
			m.open();
		}
		if (good)
		{
			if (fuzzy)
			{
				addFuzzyResponseFilter(transaction, threshold, matches, intercept);
			}
			else
			{
				addStandardFilter((InterceptFilterLocation) componentCombo.getData(componentCombo.getText()),
						patternText.getText(), matches, intercept);
			}
			patternText.setText("");
		}
	}

	protected void fuzzyVisibility(boolean fuzzy)
	{
		patternText.setVisible(!fuzzy);

		transactionNumberLabel.setVisible(fuzzy);
		transactionNumberText.setVisible(fuzzy);
		thresholdLabel.setVisible(fuzzy);
		thresholdText.setVisible(fuzzy);
	}

	protected void removeButtonWidgetSelected(SelectionEvent evt)
	{
		int index = criteriaTable.getSelectionIndex();
		if (index >= 0)
		{
			TableItem item = criteriaTable.getItem(index);
			InterceptFilter filter = (InterceptFilter) item.getData();
			changeHandler.removeFilter(filter);

			if (filter instanceof FuzzyResponseFilter)
			{
				FuzzyResponseFilter ffilter = (FuzzyResponseFilter) filter;
				fuzzyVisibility(true);
				thresholdText.setText(String.valueOf(ffilter.getThreshold()));
				transactionNumberText.setText(String.valueOf(ffilter.getTransactionID()));
			}
			else
			{
				fuzzyVisibility(false);
				patternText.setText(item.getText(2));
			}
			componentCombo.setText(item.getText(0));
			matchesCombo.setText(item.getText(1));
			actionCombo.setText(item.getText(3));
			criteriaTable.remove(index);
			item.dispose();
		}
	}

	protected void resetButtonWidgetSelected(SelectionEvent evt)
	{
		TableItem items[] = criteriaTable.getItems();

		for (TableItem item : items)
		{
			InterceptFilter data = (InterceptFilter) item.getData();
			item.dispose();
		}
		criteriaTable.removeAll();
	}

}
