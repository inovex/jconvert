package com.edsdev.jconvert.presentation.component.test;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class MyListModel implements ListModel {

	List listeners = null;

	List items = null;

	public MyListModel(List pItems) {
		items = pItems;
	}

	public void addListDataListener(ListDataListener l) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(l);
	}

	public Object getElementAt(int index) {
		if (items == null) {
			return null;
		}
		return items.get(index);
	}

	public int getSize() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	public void removeListDataListener(ListDataListener l) {
		if (listeners != null) {
			listeners.remove(l);
		}
	}

}
