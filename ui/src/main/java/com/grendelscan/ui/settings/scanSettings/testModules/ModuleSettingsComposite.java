package com.grendelscan.ui.settings.scanSettings.testModules;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

import com.grendelscan.scan.Scan;
import com.grendelscan.testing.misc.ModuleDependencyException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.MasterTestModuleCollection;
import com.grendelscan.testing.modules.settings.ConfigurationOption;
import com.grendelscan.testing.modules.settings.FileNameOption;
import com.grendelscan.testing.modules.settings.IntegerOption;
import com.grendelscan.testing.modules.settings.MultiSelectOptionGroup;
import com.grendelscan.testing.modules.settings.OptionGroup;
import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.SingleSelectOptionGroup;
import com.grendelscan.testing.modules.settings.TextListOption;
import com.grendelscan.testing.modules.settings.TextOption;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.MainWindow;
import com.grendelscan.ui.customControls.AddRemoveHandler;
import com.grendelscan.ui.customControls.AddRemoveListComposite;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GSashForm;
import com.grendelscan.ui.customControls.basic.GScrolledComposite;
import com.grendelscan.ui.customControls.basic.GText;
import com.grendelscan.ui.customControls.basic.GTree;
import com.grendelscan.ui.customControls.basic.GTreeItem;
import com.grendelscan.ui.settings.GrendelSettingsControl;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ModuleSettingsComposite extends com.grendelscan.ui.customControls.basic.GComposite implements GrendelSettingsControl
{

    {
        // Register as a resource user - SWTResourceManager will
        // handle the obtaining and disposing of resources
        GuiUtils.registerResourceUser(this);
    }

    protected GSashForm testModuleTabSashForm;
    protected GSashForm testModuleDetailsSashForm;
    protected GComposite testModuleSettingsComposite;
    protected GScrolledComposite testModuleSettingsScrolledComposite;
    protected GText testModuleDescriptionTextArea;
    protected GComposite testModuleDescriptionComposite;
    protected GTreeItem allTestModulesTreeItem;
    protected GTree testModuleTree;
    protected GLabel experimentalLabel;
    protected GComposite testModuleTreeComposit;

    public ModuleSettingsComposite(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style)
    {
        super(parent, style);
        initGUI();
    }

    void changeCheckChildren(final TreeItem child2, final boolean checked) throws ModuleDependencyException
    {
        child2.setChecked(checked);
        for (TreeItem child : child2.getItems())
        {
            changeCheckChildren(child, checked);
        }
        if (child2.getData() instanceof AbstractTestModule)
        {
            AbstractTestModule module = (AbstractTestModule) child2.getData();
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

    private void checkModuleTreeBranch(final AbstractTestModule module)
    {
        GTreeItem currentNode = allTestModulesTreeItem;
        String path = module.getGUIDisplayPath().getText();
        for (String nodeName : path.split("\\\\"))
        {
            GTreeItem nextNode = (GTreeItem) currentNode.getData(nodeName);
            if (nextNode == null)
            {
                GTreeItem newNode;
                newNode = new GTreeItem(currentNode, SWT.NONE);
                newNode.setText(nodeName);
                newNode.setExpanded(true);
                currentNode.setData(nodeName, newNode);
                nextNode = newNode;
            }
            currentNode = nextNode;
        }

        GTreeItem moduleTreeItem = new GTreeItem(currentNode, SWT.CHECK);
        moduleTreeItem.setText((module.isExperimental() ? "* " : "") + module.getName());
        moduleTreeItem.setData(module);
        moduleTreeItem.setChecked(Scan.getInstance().isModuleEnabled(module));
        currentNode.setExpanded(true);
    }

    /**
     * Overriding checkSubclass allows this class to extend com.grendelscan.ui.customControls.basic.GComposite
     */
    @Override
    protected void checkSubclass()
    {
    }

    protected void clearModuleSettingsArea()
    {
        for (Control child : testModuleSettingsComposite.getChildren())
        {
            child.dispose();
        }
        testModuleSettingsComposite.setBounds(0, 0, 800, 30);

    }

    private void initGUI()
    {
        GridLayout thisLayout = new GridLayout();
        setLayout(thisLayout);
        {
            {
                testModuleTabSashForm = new GSashForm(this, SWT.HORIZONTAL | SWT.H_SCROLL);
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
                    testModuleTreeComposit = new GComposite(testModuleTabSashForm, SWT.NONE);
                    FormLayout testModuleTreeCompositLayout = new FormLayout();
                    testModuleTreeComposit.setLayout(testModuleTreeCompositLayout);
                    {
                        experimentalLabel = new GLabel(testModuleTreeComposit, SWT.NONE);
                        FormData experimentalLabelLData = new FormData();
                        experimentalLabelLData.width = 171;
                        experimentalLabelLData.height = 17;
                        experimentalLabelLData.left = new FormAttachment(0, 1000, 9);
                        experimentalLabelLData.bottom = new FormAttachment(1000, 1000, -5);
                        experimentalLabel.setLayoutData(experimentalLabelLData);
                        experimentalLabel.setText("* Experimental test module");
                    }
                    {
                        testModuleTree = new GTree(testModuleTreeComposit, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
                        FormData testModuleTreeLData = new FormData();
                        testModuleTreeLData.width = 509;
                        testModuleTreeLData.height = 515;
                        testModuleTreeLData.left = new FormAttachment(0, 1000, 5);
                        testModuleTreeLData.right = new FormAttachment(1000, 1000, -5);
                        testModuleTreeLData.top = new FormAttachment(0, 1000, 5);
                        testModuleTreeLData.bottom = new FormAttachment(1000, 1000, -30);
                        testModuleTree.setLayoutData(testModuleTreeLData);
                        testModuleTree.addSelectionListener(new SelectionAdapter()
                        {
                            @Override
                            public void widgetSelected(final SelectionEvent event)
                            {
                                // Debug.debug("event id " + event.detail);
                                GTreeItem item = (GTreeItem) event.item;
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
                                    if (!(item.getData() instanceof AbstractTestModule))
                                    {
                                        item.setExpanded(true);
                                    }
                                }
                                // I would think that SWT.SELECTED would make more sense, but it's returning
                                // a 0
                                else if (event.detail == 0)
                                {
                                    if (item.getData() instanceof AbstractTestModule)
                                    {
                                        AbstractTestModule module = (AbstractTestModule) item.getData();
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
                            allTestModulesTreeItem = new GTreeItem(testModuleTree, SWT.NONE);
                            allTestModulesTreeItem.setText("All test modules");
                            allTestModulesTreeItem.setExpanded(true);
                        }
                    }
                }
                {
                    testModuleDetailsSashForm = new GSashForm(testModuleTabSashForm, SWT.VERTICAL | SWT.V_SCROLL);
                    testModuleDetailsSashForm.setLayout(new GridLayout());
                    {
                        testModuleDescriptionComposite = new GComposite(testModuleDetailsSashForm, SWT.NONE);
                        GridLayout testModuleDescriptionCompositeLayout = new GridLayout();
                        testModuleDescriptionCompositeLayout.makeColumnsEqualWidth = true;
                        testModuleDescriptionComposite.setLayout(testModuleDescriptionCompositeLayout);
                        {
                            testModuleDescriptionTextArea = new GText(testModuleDescriptionComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
                            GridData testModuleDescriptionTextAreaLData = new GridData();
                            testModuleDescriptionTextAreaLData.verticalAlignment = GridData.FILL;
                            testModuleDescriptionTextAreaLData.horizontalAlignment = GridData.FILL;
                            testModuleDescriptionTextAreaLData.grabExcessHorizontalSpace = true;
                            testModuleDescriptionTextAreaLData.grabExcessVerticalSpace = true;
                            testModuleDescriptionTextArea.setLayoutData(testModuleDescriptionTextAreaLData);
                            testModuleDescriptionTextArea.setEditable(false);
                            testModuleDescriptionTextArea.setBackground(GuiUtils.getColor(255, 255, 255));
                        }
                        testModuleDescriptionComposite.layout();
                    }
                    {
                        testModuleSettingsScrolledComposite = new GScrolledComposite(testModuleDetailsSashForm, SWT.V_SCROLL | SWT.H_SCROLL);
                        {
                            testModuleSettingsComposite = new GComposite(testModuleSettingsScrolledComposite, SWT.NONE);
                            RowLayout testModuleSettingsCompositeLayout = new RowLayout(SWT.VERTICAL);
                            // testModuleSettingsCompositeLayout.makeColumnsEqualWidth = true;
                            testModuleSettingsComposite.setLayout(testModuleSettingsCompositeLayout);
                            testModuleSettingsScrolledComposite.setContent(testModuleSettingsComposite);
                            // testModuleSettingsComposite.setBounds(0, 0, 525, 30);
                        }
                    }
                }
            }
            testModuleDetailsSashForm.setWeights(new int[] { 20, 80 });
            testModuleTabSashForm.setWeights(new int[] { 35, 65 });
        }
        this.layout();
    }

    protected void makeExperimentalText(final String statusText, final GComposite parent)
    {

        GLabel label = new GLabel(parent, SWT.NONE);
        label.setText("Experimental status:");

        int style = SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL;
        GText textBox = new GText(parent, style);
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

        // GridData text1LData = new GridData();
        // text1LData.widthHint = 400;
        // text1LData.heightHint = 100;
        // textBox.setLayoutData(text1LData);

        parent.layout();
        if (parent == testModuleSettingsComposite)
        {
            Rectangle b = parent.getBounds();
            b.height += textBox.getBounds().height;
            parent.setBounds(b);
            parent.layout();
        }

    }

    protected void makeFileNameOption(final FileNameOption option, final GComposite parent)
    {
        makeOptionLabel(option, parent);
        GText filenameTextBox = new GText(parent, SWT.BORDER);
        filenameTextBox.addModifyListener(new TextModifyListener(option, filenameTextBox));
        filenameTextBox.setSize(300, 25);
        // GridData text1LData = new GridData();
        // text1LData.widthHint = 300;
        // text1LData.heightHint = 25;
        // filenameTextBox.setLayoutData(text1LData);
        filenameTextBox.setText(option.getValue());

        GButton filenameBrowseButton = new GButton(parent, SWT.PUSH | SWT.CENTER);
        filenameBrowseButton.setText("Browse");
        filenameBrowseButton.setSize(100, 25);
        filenameBrowseButton.addSelectionListener(new FileNameBrowseButtonListener(option, filenameTextBox));

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

    protected void makeGroupOptionGUI(final OptionGroup option, final GComposite parent)
    {
        GGroup group = new GGroup(parent, SWT.NONE);
        group.setText(((ConfigurationOption) option).getName());

        GridLayout groupLayout = new GridLayout();
        groupLayout.makeColumnsEqualWidth = true;
        group.setLayout(groupLayout);

        if (option instanceof MultiSelectOptionGroup)
        {
            MultiSelectOptionGroup mgroup = (MultiSelectOptionGroup) option;
            for (SelectableOption soption : mgroup.getAllOptions())
            {
                makeSelectableOptionGUI(soption, group, true);
            }
        }
        else if (option instanceof SingleSelectOptionGroup)
        {
            SingleSelectOptionGroup sgroup = (SingleSelectOptionGroup) option;
            for (SelectableOption soption : sgroup.getAllOptions())
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

    protected void makeIntegerOptionGUI(final IntegerOption option, final GComposite parent)
    {
        makeOptionLabel(option, parent);

        GText textBox = new GText(parent, SWT.BORDER);
        textBox.addModifyListener(new IntegerModifyListener(option, textBox));

        // GridData text1LData = new GridData();
        // text1LData.widthHint = 300;
        // text1LData.heightHint = 25;
        // textBox.setLayoutData(text1LData);

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

    protected void makeOptionLabel(final ConfigurationOption option, final GComposite parent)
    {
        GLabel label = new GLabel(parent, SWT.NONE);
        label.setText(option.getName());
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
     * @param checkBox
     *            If false, then a radio button will be created
     */
    protected void makeSelectableOptionGUI(final SelectableOption option, final Composite parent, final boolean checkBox)
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

        GButton button = new GButton(parent, style);
        button.setData(option);
        button.setText(option.getName());
        button.setSelection(option.isSelected());
        button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent event)
            {
                GButton b = (GButton) event.getSource();
                SelectableOption so = (SelectableOption) b.getData();
                so.setSelected(b.getSelection());
            }
        });

        parent.layout();
        if (parent == testModuleSettingsComposite)
        {
            Rectangle b = parent.getBounds();
            b.height += button.getBounds().height;
            parent.setBounds(b);
            parent.layout();
        }
    }

    protected void makeTextListOption(final TextListOption option, final GComposite parent)
    {
        makeOptionLabel(option, parent);

        AddRemoveHandler handler = new AddRemoveHandler()
        {
            @SuppressWarnings("unused")
            @Override
            public void addItem(final String item) throws Throwable
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
            public void removeItem(final String item) throws Throwable
            {
                option.add(item);
            }
        };
        AddRemoveListComposite list = new AddRemoveListComposite(parent, 0, option.getName(), handler, option.getReadOnlyData().toArray(new String[0]));

        // GridData text1LData = new GridData();
        // text1LData.widthHint = 300;
        // text1LData.heightHint = 100;
        // list.setLayoutData(text1LData);

        parent.layout();
        if (parent == testModuleSettingsComposite)
        {
            Rectangle b = parent.getBounds();
            b.height += list.getBounds().height;
            parent.setBounds(b);
            parent.layout();
        }
    }

    protected void makeTextOptionGUI(final TextOption option, final GComposite parent)
    {
        makeOptionLabel(option, parent);

        int style = SWT.BORDER | (option.isMultiLine() ? SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : 0);
        GText textBox = new GText(parent, style);
        textBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
        textBox.addModifyListener(new TextModifyListener(option, textBox));

        // GridData text1LData = new GridData();
        // text1LData.widthHint = 300;
        // text1LData.heightHint = option.isMultiLine() ? 100 : 25;
        // textBox.setLayoutData(text1LData);

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

    public void populateModules()
    {
        testModuleTree.removeAll();
        allTestModulesTreeItem = new GTreeItem(testModuleTree, SWT.NONE);
        allTestModulesTreeItem.setText("All test modules");
        allTestModulesTreeItem.setExpanded(true);
        for (AbstractTestModule module : MasterTestModuleCollection.getInstance().getAllTestModules())
        {
            if (!module.hidden())
            {
                checkModuleTreeBranch(module);
            }
        }
        allTestModulesTreeItem.setExpanded(true);
    }

    protected void populateTestModuleSettings(final AbstractTestModule module)
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
                GLabel defaultLabel = new GLabel(testModuleSettingsComposite, SWT.NONE);
                defaultLabel.setText("No configurable options for " + module.getName());

                Rectangle b = testModuleSettingsComposite.getBounds();
                b.height += defaultLabel.getBounds().height;
                testModuleSettingsComposite.setBounds(b);
                testModuleSettingsComposite.layout();
            }
            else
            {
                for (ConfigurationOption option : module.getConfigurationOptions())
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
