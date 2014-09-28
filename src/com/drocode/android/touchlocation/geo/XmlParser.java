package com.drocode.android.touchlocation.geo;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlParser {
	private final String TAG = getClass().getSimpleName();

	public GeoCodeResult parseXmlResponse(String response) {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));
			NodeList resultNodeList = doc.getElementsByTagName("Result");
			Node resultNode = resultNodeList.item(0);
			NodeList attrsList = resultNode.getChildNodes();

			GeoCodeResult result = new GeoCodeResult();
			for (int i = 0; i < attrsList.getLength(); i++) {
				Node node = attrsList.item(i);
				Node firstChild = node.getFirstChild();

				if ("street".equalsIgnoreCase(node.getNodeName())
						&& firstChild != null) {
					result.street = firstChild.getNodeValue();
				}
				if ("neighborhood".equalsIgnoreCase(node.getNodeName())
						&& firstChild != null) {
					result.neighborhood = firstChild.getNodeValue();
				}
				if ("city".equalsIgnoreCase(node.getNodeName())
						&& firstChild != null) {
					result.city = firstChild.getNodeValue();
				}
				if ("postal".equalsIgnoreCase(node.getNodeName())
						&& firstChild != null) {
					result.postal = firstChild.getNodeValue();
				}
				if ("state".equalsIgnoreCase(node.getNodeName())
						&& firstChild != null) {
					result.state = firstChild.getNodeValue();
				}
				if ("country".equalsIgnoreCase(node.getNodeName())
						&& firstChild != null) {
					result.country = firstChild.getNodeValue();
				}
			}

			Log.d(TAG, result.toString());

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
