package de.menski.robosim;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class AddFieldsDialog extends Dialog {
	
	public static final int LEFT = 0;
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 3;
	
	public int result;
	public int number;
	public int dest;

	public AddFieldsDialog(Shell parent, int style) {
		super(parent, style);
		number = 1;
		dest = LEFT;
		result = SWT.CANCEL;
	}
	
	public AddFieldsDialog(Shell parent) {
		this(parent, 0);
	}

	public int open () {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Add new Columns/Lines");
		shell.setLayout(new FormLayout());		

		FormData formData;
	
		Label label = new Label(shell, SWT.NONE);
		label.setText("No. of Rows/Lines: ");
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(0);
		label.setLayoutData(formData);
		
		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.setText(String.valueOf(number));
		formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(label);
		formData.right = new FormAttachment(100);
		text.setLayoutData(formData);
		
		final Button bLeft = new Button(shell, SWT.RADIO);
		bLeft.setText("Left");
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(text);
		bLeft.setLayoutData(formData);
		
		final Button bTop = new Button(shell, SWT.RADIO);
		bTop.setText("Top");
		formData = new FormData();
		formData.left = new FormAttachment(bLeft);
		formData.top = new FormAttachment(text);
		bTop.setLayoutData(formData);
		
		final Button bRight = new Button(shell, SWT.RADIO);
		bRight.setText("Right");
		bRight.setSelection(true);
		formData = new FormData();
		formData.left = new FormAttachment(bTop);
		formData.top = new FormAttachment(text);
		bRight.setLayoutData(formData);
		
		final Button bBottom = new Button(shell, SWT.RADIO);
		bBottom.setText("Bottom");
		formData = new FormData();
		formData.left = new FormAttachment(bRight);
		formData.top = new FormAttachment(text);
		formData.right = new FormAttachment(100);
		bBottom.setLayoutData(formData);
		
		Button bCancel = new Button(shell, SWT.PUSH);
		bCancel.setText("Cancel");
		formData = new FormData();
		formData.top = new FormAttachment(bBottom);
		formData.left = new FormAttachment(bRight);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(100);
		bCancel.setLayoutData(formData);
		
		Button bOk = new Button(shell, SWT.PUSH);
		bOk.setText("OK");
		formData = new FormData();
		formData.top = new FormAttachment(bRight);
		formData.left = new FormAttachment(bTop);
		formData.right = new FormAttachment(bCancel);
		formData.bottom = new FormAttachment(100);
		bOk.setLayoutData(formData);
		bOk.setSize(bCancel.getSize());
		
		text.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		bCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				result = SWT.CANCEL;
				shell.close();
			}
		});

		bOk.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				number = Integer.parseInt(text.getText());
				if (bLeft.getSelection()) {
					dest = LEFT;
				}
				else if (bTop.getSelection()) {
					dest = TOP;
				}
				else if (bRight.getSelection()) {
					dest = RIGHT;
				}
				else if (bBottom.getSelection()) {
					dest = BOTTOM;
				}
				result = SWT.OK;
				shell.close();
			}
		});
		
		shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}

}
