package com.grendelscan.GUI.settings.scanSettings.testModules;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.customControls.AddRemoveHandler;
import com.grendelscan.GUI.customControls.AddRemoveListComposite;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.MasterTestModuleCollection;
import com.grendelscan.tests.testModuleUtils.ModuleDependencyException;
import com.grendelscan.tests.testModuleUtils.settings.ConfigurationOption;
import com.grendelscan.tests.testModuleUtils.settings.FileNameOption;
import com.grendelscan.tests.testModuleUtils.settings.IntegerOption;
import com.grendelscan.tests.testModuleUtils.settings.MultiSelectOptionGroup;
import com.grendelscan.tests.testModuleUtils.settings.OptionGroup;
import com.grendelscan.tests.testModuleUtils.settings.SelectableOption;
import com.grendelscan.tests.testModuleUtils.settings.SingleSelectOptionGroup;
import com.grendelscan.tests.testModuleUtils.settings.TextListOption;
import com.grendelscan.tests.testModuleUtils.settings.TextOption;
import com.grendelscan.tests.testModules.TestModule;


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
public class ModuleSettingsComposite extends org.eclipse.swt.widgets.Composite implements GrendelSettingsControl{

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	protected SashForm testModuleTabSashForm;
	protected SashForm testModuleDetailsSashForm;
	protected Composite testModuleSettingsComposite;
	protected ScrolledComposite testModuleSettingsScrolledComposite;
	protected Text testModuleDescriptionTextArea;
	protected Composite testModuleDescriptionComposite;
	protected TreeItem allTestModulesTreeItem;
	protected Tree testModuleTree;
	protected Label experimentalLabel;
	protected Composite testModuleTreeComposit;

	
	/**
	* Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	*/	
	@Override
	protected void checkSubclass() {
	}
	

	public ModuleSettingsComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			GridLayout thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			{
				{
					testModuleTabSashForm = new SashForm(this, SWT.HORIZONTAL | SWT.H_SCROLL);
					GridLayout gridLayout = new GridLayout();
					testModuleTabSashForm.setLayout(gridLayout);
					GridData testModuleTabSashFormLData = new GridData();
					testModuleTabSashFormLData.verticalAlignment = GridData.FILL;
					testModuleTabSashFormLData.horizontalAlignment = GridData.FILL;
					testModuleTabSashFormLData.grabExcessHorizontalSpace = true;
					testModuleTabSashFormLData.grabExcessVerticalSpace = true;
					testModuleTabSashForm.setLayoutData(testModuleTabSashFormLData);
					testModuleTabSashForm.setBounds(0, 0, 875, 557);
					{
						testModuleTreeComposit = new Composite(testModuleTabSashForm, SWT.NONE);
						FormLayout testModuleTreeCompositLayout = new FormLayout();
						testModuleTreeComposit.setLayout(testModuleTreeCompositLayout);
						{
							experimentalLabel = new Label(testModuleTreeComposit, SWT.NONE);
							FormData experimentalLabelLData = new FormData();
							experimentalLabelLData.width = 171;
							experimentalLabelLData.height = 17;
							experimentalLabelLData.left =  new FormAttachment(0, 1000, 9);
							experimentalLabelLData.bottom =  new FormAttachment(1000, 1000, -5);
							experimentalLabel.setLayoutData(experimentalLabelLData);
							experimentalLabel.setText("* Experimental test module");
						}
						{
							testModuleTree = new Tree(testModuleTreeComposit, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
							FormData testModuleTreeLData = new FormData();
							testModuleTreeLData.width = 509;
							testModuleTreeLData.height = 515;
							testModuleTreeLData.left =  new FormAttachment(0, 1000, 5);
							testModuleTreeLData.right =  new FormAttachment(1000, 1000, -5);
							testModuleTreeLData.top =  new FormAttachment(0, 1000, 5);
							testModuleTreeLData.bottom =  new FormAttachment(1000, 1000, -30);
							testModuleTree.setLayoutData(testModuleTreeLData);
							testModuleTree.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
							testModuleTree.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent event) {
									//													Debug.debug("event id " + event.detail);
									TreeItem item = (TreeItem) event.item;
									if (event.detail == SWT.CHECK)
									{
										try
										{
											changeCheckChildren(item, item.getChecked());
										}
										catch (ModuleDependencyException e)
										{
											MainWindow.getInstance().displayMessage("Error", "Cannot disable module yet: " + e.getMessage(), true);
										}
										if (!(item.getData() instanceof TestModule))
										{
											item.setExpanded(true);
										}
									}
									// I would think that SWT.SELECTED would make more sense, but it's returning
									// a 0
									else if (event.detail == 0)
									{
										if (item.getData() instanceof TestModule)
										{
											TestModule module = (TestModule) item.getData();
											testModuleDescriptionTextArea.setText(module.getDescription());
											populateTestModuleSettings(module);
										}
										else
										{
											testModuleDescriptionTextArea.setText("");
											populateTestModuleSettings(null);
											item.setExpanded(true);
										}
									}
								}
							});
							{
								allTestModulesTreeItem = new TreeItem(testModuleTree, SWT.NONE);
								allTestModulesTreeItem.setText("All test modules");
								allTestModulesTreeItem.setExpanded(true);
							}
						}
					}
					{
						testModuleDetailsSashForm = new SashForm(testModuleTabSashForm, SWT.VERTICAL | SWT.V_SCROLL);
						testModuleDetailsSashForm.setLayout(new GridLayout());
						{
							testModuleDescriptionComposite = new Composite(testModuleDetailsSashForm, SWT.NONE);
							GridLayout testModuleDescriptionCompositeLayout = new GridLayout();
							testModuleDescriptionCompositeLayout.makeColumnsEqualWidth = true;
							testModuleDescriptionComposite.setLayout(testModuleDescriptionCompositeLayout);
							{
								testModuleDescriptionTextArea = new Text(testModuleDescriptionComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
								testModuleDescriptionTextArea.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
								GridData testModuleDescriptionTextAreaLData = new GridData();
								testModuleDescriptionTextAreaLData.verticalAlignment = GridData.FILL;
								testModuleDescriptionTextAreaLData.horizontalAlignment = GridData.FILL;
								testModuleDescriptionTextAreaLData.grabExcessHorizontalSpace = true;
								testModuleDescriptionTextAreaLData.grabExcessVerticalSpace = true;
								testModuleDescriptionTextArea.setLayoutData(testModuleDescriptionTextAreaLData);
								testModuleDescriptionTextArea.setEditable(false);
								testModuleDescriptionTextArea.setBackground(SWTResourceManager.getColor(255,255,255));
							}
							testModuleDescriptionComposite.layout();
						}
						{
							testModuleSettingsScrolledComposite = new ScrolledComposite(testModuleDetailsSashForm, SWT.V_SCROLL | SWT.H_SCROLL);
							{
								testModuleSettingsComposite = new Composite(testModuleSettingsScrolledComposite, SWT.NONE);
								RowLayout testModuleSettingsCompositeLayout = new RowLayout(SWT.VERTICAL);
//								testModuleSettingsCompositeLayout.makeColumnsEqualWidth = true;
								testModuleSettingsComposite.setLayout(testModuleSettingsCompositeLayout);
								testModuleSettingsScrolledComposite.setContent(testModuleSettingsComposite);
//								testModuleSettingsComposite.setBounds(0, 0, 525, 30);
							}
						}
					}
				}
				testModuleDetailsSashForm.setWeights(new int[]{20,80});
				testModuleTabSashForm.setWeights(new int[]{35,65});
			}
			this.layout();
	}
	
	
	void changeCheckChildren(TreeItem node, boolean checked) throws ModuleDependencyException
	{
		node.setChecked(checked);
		for (TreeItem child: node.getItems())
		{
			changeCheckChildren(child, checked);
		}
		if (node.getData() instanceof TestModule)
		{
			TestModule module = (TestModule) node.getData();
			if (checked)
			{
				Scan.getInstance().enableTestModule(module.getClass());
			}
			else
			{
				Scan.getInstance().disableTestModule(module.getClass());
			}
		}
	}
	
	public void populateModules()
	{
		testModuleTree.removeAll();
		allTestModulesTreeItem = new TreeItem(testModuleTree, SWT.NONE);
		allTestModulesTreeItem.setText("All test modules");
		allTestModulesTreeItem.setExpanded(true);
		for (TestModule module: MasterTestModuleCollection.getInstance().getAllTestModules())
		{
			if (!module.hidden())
			{
				checkModuleTreeBranch(module);
			}
		}
		allTestModulesTreeItem.setExpanded(true);
	}
	
	
	private void checkModuleTreeBranch(TestModule module)
	{
		TreeItem currentNode = allTestModulesTreeItem;
		String path = module.getGUIDisplayPath().getText();
		for (String nodeName: path.split("\\\\"))
		{
			TreeItem nextNode = (TreeItem) currentNode.getData(nodeName);
			if (nextNode == null)
			{
				TreeItem newNode;
				newNode = new TreeItem(currentNode, SWT.NONE);
				newNode.setText(nodeName);
				newNode.setExpanded(true);
				currentNode.setData(nodeName, newNode);
				nextNode = newNode;
			}
			currentNode = nextNode;
		}

		TreeItem moduleTreeItem = new TreeItem(currentNode, SWT.CHECK);
		moduleTreeItem.setText((module.isExperimental() ? "* ": "") + module.getName());
		moduleTreeItem.setData(module);
		moduleTreeItem.setChecked(Scan.getInstance().isModuleEnabled(module));
		currentNode.setExpanded(true);
	}
	
	
	protected void populateTestModuleSettings(TestModule module)
	{
		clearModuleSettingsArea();

		if (module != null)
		{
			if (module.isExperimental())
			{
				makeExperimentalText(module.getExperimentalText(), testModuleSettingsComposite);
			}
			
			if (module.getConfigurationOptions().size() == 0)
			{
				Label defaultLabel = new Label(testModuleSettingsComposite, SWT.NONE);
				defaultLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
				defaultLabel.setText("No configurable options for " + module.getName());

				Rectangle b = testModuleSettingsComposite.getBounds();
				b.height += defaultLabel.getBounds().height;
				testModuleSettingsComposite.setBounds(b);
				testModuleSettingsComposite.layout();
			}
			else
			{
				for(ConfigurationOption option: module.getConfigurationOptions())
				{
					
					if (option instanceof OptionGroup)
					{
						makeGroupOptionGUI((OptionGroup) option, testModuleSettingsComposite);
					}
					else if (option instanceof FileNameOption)
					{
						makeFileNameOption((FileNameOption) option, testModuleSettingsComposite);
					}
					else if (option instanceof IntegerOption)
					{
						makeIntegerOptionGUI((IntegerOption) option, testModuleSettingsComposite);
					}
					else if (option instanceof TextOption)
					{
						makeTextOptionGUI((TextOption) option, testModuleSettingsComposite);
					}
					else if (option instanceof SelectableOption)
					{
						makeSelectableOptionGUI((SelectableOption) option, testModuleSettingsComposite, true);
					}
					else if (option instanceof TextListOption)
					{
						makeTextListOption((TextListOption) option, testModuleSettingsComposite);
					}
				}
			}
		}

		testModuleSettingsComposite.getParent().layout();
		testModuleSettingsComposite.layout();
	}
	
	
	protected void makeTextListOption(final TextListOption option, Composite parent)
	{
		makeOptionLabel(option, parent);
		
		AddRemoveHandler handler = new AddRemoveHandler()
		{
			@SuppressWarnings("unused")
			@Override
			public void removeItem(String item) throws Throwable
			{
				option.add(item);
			}
			
			@SuppressWarnings("unused")
			@Override
			public void clear() throws Throwable
			{
				option.clear();
			}
			
			@SuppressWarnings("unused")
			@Override
			public void addItem(String item) throws Throwable
			{
				option.add(item);
			}
		};
		AddRemoveListComposite list = new AddRemoveListComposite(
				parent, 0, option.getName(), handler, option.getReadOnlyData().toArray(new String[0]));
		list.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		
//		GridData text1LData = new GridData();
//		text1LData.widthHint = 300;
//		text1LData.heightHint = 100;
//		list.setLayoutData(text1LData);

		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += list.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}
	}
	
	
	protected void makeExperimentalText(String statusText, Composite parent)
	{
		
		Label label = new Label(parent, SWT.NONE);
		label.setText("Experimental status:");
		label.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		
		int style = SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL;
		Text textBox = new Text(parent, style);
		textBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		textBox.setText(statusText);
		textBox.setBounds(0, 0, 400, 100);

		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += label.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}

		//		GridData text1LData = new GridData();
//		text1LData.widthHint = 400;
//		text1LData.heightHint = 100;
//		textBox.setLayoutData(text1LData);

		
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += textBox.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}

	}
	
	protected void makeIntegerOptionGUI(IntegerOption option, Composite parent)
	{
		makeOptionLabel(option, parent);
		
		Text textBox = new Text(parent, SWT.BORDER);
		textBox.addModifyListener(new IntegerModifyListener(option, textBox));
		textBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		
//		GridData text1LData = new GridData();
//		text1LData.widthHint = 300;
//		text1LData.heightHint = 25;
//		textBox.setLayoutData(text1LData);

		textBox.setText(String.valueOf(option.getValue()));
		
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += textBox.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}
	}


	
	protected void makeTextOptionGUI(TextOption option, Composite parent)
	{
		makeOptionLabel(option, parent);
		
		int style = SWT.BORDER | (option.isMultiLine() ? SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : 0);
		Text textBox = new Text(parent, style);
		textBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		textBox.addModifyListener(new TextModifyListener(option, textBox));
		
//		GridData text1LData = new GridData();
//		text1LData.widthHint = 300;
//		text1LData.heightHint = option.isMultiLine() ? 100 : 25;
//		textBox.setLayoutData(text1LData);

		textBox.setText(option.getValue());
		
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += textBox.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}
	}
	
	
	protected void makeGroupOptionGUI(OptionGroup option, Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(((ConfigurationOption) option).getName());
		group.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));

		GridLayout groupLayout = new GridLayout();
		groupLayout.makeColumnsEqualWidth = true;
		group.setLayout(groupLayout);
		
		if (option instanceof MultiSelectOptionGroup)
		{
			MultiSelectOptionGroup mgroup = (MultiSelectOptionGroup) option;
			for (SelectableOption soption: mgroup.getAllOptions())
			{
				makeSelectableOptionGUI(soption, group, true);
			}
		}
		else if (option instanceof SingleSelectOptionGroup)
		{
			SingleSelectOptionGroup sgroup = (SingleSelectOptionGroup) option;
			for (SelectableOption soption: sgroup.getAllOptions())
			{
				makeSelectableOptionGUI(soption, group, false);
			}
		}
		
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += group.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}
	}

	
	
	protected void makeOptionLabel(ConfigurationOption option, Composite parent)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(option.getName());
		label.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += label.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}
	}
	
	/**
	 * 
	 * @param option
	 * @param parent
	 * @param checkBox If false, then a radio button will be created
	 */
	protected void makeSelectableOptionGUI(SelectableOption option, Composite parent, boolean checkBox)
	{
		int style = SWT.LEFT;
		if (checkBox)
		{
			style |= SWT.CHECK;
		}
		else
		{
			style |= SWT.RADIO;
		}
			
		Button button = new Button(parent, style);
		button.setData(option);
		button.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		button.setText(option.getName());
		button.setSelection(option.isSelected());
		button.addSelectionListener(
			new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent event)
				{
					Button b = (Button) event.getSource();
					SelectableOption so = (SelectableOption) b.getData();
					so.setSelected(b.getSelection());
				}
			}
		);
				
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += button.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}
	}
	
	protected void clearModuleSettingsArea()
	{
		for (Control child: testModuleSettingsComposite.getChildren())
		{
			child.dispose();
		}
		testModuleSettingsComposite.setBounds(0, 0, 800, 30);
		
	}	

	protected void makeFileNameOption(FileNameOption option, Composite parent)
	{
		makeOptionLabel(option, parent);
		Text filenameTextBox = new Text(parent, SWT.BORDER);
		filenameTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
		filenameTextBox.addModifyListener(new TextModifyListener(option, filenameTextBox));
		filenameTextBox.setSize(300, 25);
//		GridData text1LData = new GridData();
//		text1LData.widthHint = 300;
//		text1LData.heightHint = 25;
//		filenameTextBox.setLayoutData(text1LData);
		filenameTextBox.setText(option.getValue());

		Button filenameBrowseButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		filenameBrowseButton.setText("Browse");
		filenameBrowseButton.setSize(100, 25);
		filenameBrowseButton.addSelectionListener(new FileNameBrowseButtonListener(option, filenameTextBox));
		filenameBrowseButton.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));

		
		parent.layout();
		if (parent == testModuleSettingsComposite)
		{
			Rectangle b = parent.getBounds();
			b.height += filenameTextBox.getBounds().height;
			b.height += filenameBrowseButton.getBounds().height;
			parent.setBounds(b);
			parent.layout();
		}

	}
	


	@Override
	public void updateFromSettings()
	{
		populateModules();
		clearModuleSettingsArea();
	}


	@Override
	public String updateToSettings()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
