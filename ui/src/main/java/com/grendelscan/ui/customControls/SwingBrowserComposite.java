package com.grendelscan.ui.customControls;

import java.awt.Frame;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;


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
public class SwingBrowserComposite extends com.grendelscan.ui.customControls.basic.GComposite {

	private JEditorPane editorPanel;
	private Frame frame;
	private JScrollPane scrollPane;
	/**
	* Auto-generated main method to display this 
	* com.grendelscan.GUI.customControls.basic.GComposite inside a new GShell.
	*/

	public SwingBrowserComposite(com.grendelscan.ui.customControls.basic.GComposite parent, int style) {
		super(parent, style | SWT.EMBEDDED | SWT.NO_BACKGROUND);
		initGUI();
	}

	private void initGUI() {
			FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			frame = SWT_AWT.new_Frame(this);
			scrollPane = new JScrollPane();
			editorPanel = new JEditorPane();
			scrollPane.setViewportView(editorPanel);
			frame.add(scrollPane);
			editorPanel.setContentType("text/html");
			editorPanel.setEditable(false);
//			editorPanel.addHyperlinkListener(
//					new HyperlinkListener()
//					{
//						public void hyperlinkUpdate(HyperlinkEvent event) 
//						{
//							if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
//							{
//								Object url = event.getURL();
//								try
//                                {
//	                                URL u = new URL(event.getDescription());
//	                                System.out.println(u.toExternalForm());
//                                }
//                                catch (MalformedURLException e)
//                                {
//	                                // TODO Auto-generated catch block
//	                                e.printStackTrace();
//                                }
//								if (url != null)
//								{
//									System.out.println(event.getURL());
//									try 
//									{
//										editorPanel.setPage(event.getURL());
//									}
//									catch(IOException ioe) 
//									{
//									}
//								}
//							}
//						}
//					});
			this.layout();
	}

	public void setHtmlText(String html)
	{
//		Tidy tidy = new Tidy();
//		tidy.setXHTML(false);
//		tidy.setXmlOut(false);
//		tidy.setMakeClean(true);
//		InputStream  is = new ByteArrayInputStream(html.getBytes());
//		ByteArrayOutputStream os = new ByteArrayOutputStream(html.length());
//		tidy.parse(is, os);
//		String h = os.toString();
//		editorPanel.setText(os.toString());
		editorPanel.setText(html);
	}
}
