package com.grendelscan.GUI.statusComposites;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import com.grendelscan.GUI.customControls.basic.GGroup;
import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.scan.Scan;

public class LogComposite extends com.grendelscan.GUI.customControls.basic.GComposite
{


	private GLabel	categorizerQueueLabel;
	private GText	categorizerQueueSizeTextBox;
	private GLabel	requesterQueueLabel;
	private GText	requesterQueueSizeTextBox;
	GText	scanStatusTextArea;
	private GText	testerQueueSizeTextBox;
	private GLabel	testQueueLabel;

	public LogComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	private void initGUI()
	{
			FormLayout logLayout = new FormLayout();
			setLayout(logLayout);
			GGroup queueSizeGroup = new GGroup(this, SWT.NONE);
			queueSizeGroup.setLayout(null);
			FormData queueSizeGroupLData = new FormData();
			queueSizeGroupLData.width = 471;
			queueSizeGroupLData.height = 51;
			queueSizeGroupLData.left = new FormAttachment(0, 1000, 5);
			queueSizeGroupLData.bottom = new FormAttachment(1000, 1000, -5);
			queueSizeGroup.setLayoutData(queueSizeGroupLData);
			queueSizeGroup.setText("Queue Sizes");
			{
				testerQueueSizeTextBox = new GText(queueSizeGroup, SWT.READ_ONLY | SWT.BORDER);
				testerQueueSizeTextBox.setBounds(73, 31, 75, 25);
				testerQueueSizeTextBox.setBackground(GuiUtils.getColor(255, 255, 255));
			}
			{
				testQueueLabel = new GLabel(queueSizeGroup, SWT.NONE);
				testQueueLabel.setText("Tester:");
				testQueueLabel.setBounds(14, 31, 51, 25);
			}
			{
				categorizerQueueSizeTextBox = new GText(queueSizeGroup, SWT.READ_ONLY | SWT.BORDER);
				categorizerQueueSizeTextBox.setBounds(247, 31, 55, 25);
				categorizerQueueSizeTextBox.setBackground(GuiUtils.getColor(255, 255, 255));
			}
			{
				requesterQueueSizeTextBox = new GText(queueSizeGroup, SWT.READ_ONLY | SWT.BORDER);
				requesterQueueSizeTextBox.setBounds(398, 31, 55, 25);
				requesterQueueSizeTextBox.setBackground(GuiUtils.getColor(255, 255, 255));
			}
			{
				requesterQueueLabel = new GLabel(queueSizeGroup, SWT.NONE);
				requesterQueueLabel.setText("Categorizer:");
				requesterQueueLabel.setBounds(160, 31, 73, 25);
			}
			{
				categorizerQueueLabel = new GLabel(queueSizeGroup, SWT.NONE);
				categorizerQueueLabel.setText("Requester:");
				categorizerQueueLabel.setBounds(314, 31, 81, 25);
			}
			{
				scanStatusTextArea =
						new GText(this, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				FormData scanStatusTextAreaLData = new FormData();
				scanStatusTextAreaLData.width = 706;
				scanStatusTextAreaLData.height = 282;
				scanStatusTextAreaLData.left = new FormAttachment(0, 1000, 13);
				scanStatusTextAreaLData.right = new FormAttachment(1000, 1000, 0);
				scanStatusTextAreaLData.bottom = new FormAttachment(1000, 1000, -80);
				scanStatusTextAreaLData.top = new FormAttachment(0, 1000, 16);
				scanStatusTextArea.setLayoutData(scanStatusTextAreaLData);
			}

			this.layout();
	}

	/**
	 * Overriding checkSubclass allows this class to extend
	 * com.grendelscan.GUI.customControls.basic.GComposite
	 */
	@Override
	protected void checkSubclass()
	{
	}
	
	public void updateQueueSizes()
	{
		testerQueueSizeTextBox.setText(String.valueOf(Scan.getInstance().getTesterQueue().getQueueLength()));
		categorizerQueueSizeTextBox.setText(String.valueOf(Scan.getInstance().getCategorizerQueue().getQueueLength()));
		requesterQueueSizeTextBox.setText(String.valueOf(Scan.getInstance().getRequesterQueue().getQueueLength()));
	}

	protected int maxStatusSize = 100000;
	int statusMessageUpdateTime = 0;

	public synchronized void appendStatusText(String text)
	{
		if (!isDisposed())
		{
			getDisplay().asyncExec(new StatusMessage(text));
		}
	}

	private class StatusMessage implements Runnable
	{
		private String text;
		
		public StatusMessage(String text)
		{
			this.text = text;
		}
		
		@Override
		public void run()
		{
			long start = Calendar.getInstance().getTimeInMillis();
			scanStatusTextArea.append(text);
			String newText = scanStatusTextArea.getText();
			if (newText.length() > maxStatusSize * 2)
			{
				int startPosition = newText.length() - maxStatusSize;
				scanStatusTextArea.setText(newText.substring(startPosition));
			}
			long end = Calendar.getInstance().getTimeInMillis();
			statusMessageUpdateTime += end - start;
		}
	}

	public long getStatusMessageUpdateTime()
	{
		return statusMessageUpdateTime;
	}
}
