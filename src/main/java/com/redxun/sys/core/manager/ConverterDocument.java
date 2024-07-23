package com.redxun.sys.core.manager;
import javax.annotation.Resource;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyValue;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.PaperFormat;
import com.sun.star.view.XPrintable;
import org.springframework.stereotype.Service;

import com.redxun.core.dao.IDao;
import com.redxun.core.manager.BaseManager;
import com.redxun.sys.core.dao.SysFormGroupDao;
import com.redxun.sys.core.entity.SysFormGroup;


@Service
public class ConverterDocument extends OpenOfficeDocumentConverter {

	public ConverterDocument(OpenOfficeConnection connection) {
		super(connection);
	}
	public final static Size A4, A3,A2,A1;
	public final static Size B4, B3, B2,B1;
	public final static Size KaoqinReport;

	static {
		A4 = new Size(21000, 29700);
		A3 = new Size(29700, 42000);
		A2 = new Size(42000, 59400);
		A1 = new Size(60000, 90000);

		B4 = new Size(25000, 35300);
		B3 = new Size(35300, 50000);
		B2 = new Size(50000, 70700);
		B1 = new Size(70700, 100000);

		KaoqinReport = new Size(42000, 54300);  //最大限度  宽 1600000
	}

	@Override
	protected void refreshDocument(XComponent document) {
		super.refreshDocument(document);

		// The default paper format and orientation is A4 and portrait. To
		// change paper orientation
		// re set page size
		XPrintable xPrintable = (XPrintable) UnoRuntime.queryInterface(XPrintable.class, document);
		PropertyValue[] printerDesc = new PropertyValue[2];

		// Paper Orientation
		//  printerDesc[0] = new PropertyValue();
		//  printerDesc[0].Name = "PaperOrientation";
		//  printerDesc[0].Value = PaperOrientation.PORTRAIT;

		// Paper Format
		printerDesc[0] = new PropertyValue();
		printerDesc[0].Name = "PaperFormat";
		printerDesc[0].Value = PaperFormat.USER;

		// Paper Size
		printerDesc[1] = new PropertyValue();
		printerDesc[1].Name = "PaperSize";
		printerDesc[1].Value = A1;

		try {
			xPrintable.setPrinter(printerDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}