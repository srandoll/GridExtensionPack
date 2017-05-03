package org.vaadin.teemusa.gridextensions.paging;

import java.util.stream.Stream;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;

public class PagedDataProvider<T, F> extends AbstractDataProvider<T, F> {

	private final PagingControls<T> pagingControls;
	final DataProvider<T, F> dataProvider;
	Integer backendSize;

	public PagedDataProvider(DataProvider<T, F> dataProvider) {
		this(dataProvider, 10);
	}

	public PagedDataProvider(DataProvider<T, F> dataProvider, int pageLength) {
		pagingControls = new PagingControls<T>(this, pageLength);
		this.dataProvider = dataProvider;
		this.dataProvider.addDataProviderListener(event -> {
			if (event instanceof DataRefreshEvent) {
				fireEvent(event);
			} else {
				setBackendSize(null);
				refreshAll();
			}
		});
	}

	@Override
	public boolean isInMemory() {
		return dataProvider.isInMemory();
	}

	@Override
	public int size(Query<T, F> query) {
		return getPagingControls().getSizeOfPage(query);
	}

	public PagingControls<T> getPagingControls() {
		return pagingControls;
	}

	void setBackendSize(Integer size) {
		backendSize = size;
		if (size != null) {
			getPagingControls().updatePageNumber();
		}
	}

	@Override
	public Stream<T> fetch(Query<T, F> query) {
		Query<T, F> newQuery = getPagingControls().alignQuery(query);
		return dataProvider.fetch(newQuery);
	}

	int getBackendSize() {
		if (backendSize == null) {
			setBackendSize(dataProvider.size(new Query<>()));
		}
		return backendSize;
	}

}
