import { searchError, completeSearch, startSearch } from '../searchResults.actions';

import { API_URL_BASE, DEFAULT_SEARCH_PAGE_SIZE } from '../../../config/constants';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import searchFields from '../../searchBuilder/searchComponentData/searchFields';

function getSearchPage(xhrHandler, pageNum) {
  return (dispatch, getState) => {
    const query = {
      page: pageNum,
      page_size: DEFAULT_SEARCH_PAGE_SIZE,
      fields: searchFields,
      queries: getState().corpex.searchResults.searchQueries,
    };

    const url = `${API_URL_BASE}/search`;
    const body = JSON.stringify(query);
    const method = 'POST';

    xhrHandler(method, url, body, startSearch, completeSearch, searchError, dispatch, getState());
  };
}

export default getSearchPage;
