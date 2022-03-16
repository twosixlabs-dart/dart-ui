import { debounce } from 'lodash';
import { completeSearch, searchError, startSearch } from '../../searchResults/searchResults.actions';

// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

const debounced = debounce((fn) => fn(), 350);

const refreshSearch = () => (xhrHandler, dispatch, getState) => {
  const {
    corpex: {
      searchResults: {
        searchQueries,
      },
      corpusView: {
        aggQueries,
      },
    },
  } = getState();

  const request = {
    page_size: 0,
    fields: ['cdr.document_id'],
    queries: searchQueries,
    aggregations: aggQueries,
  };

  const url = `${API_URL_BASE}/search`;
  const body = JSON.stringify(request);
  const method = 'POST';

  const completeAction = (res) => completeSearch(res, searchQueries, aggQueries);

  // eslint-disable-next-line max-len
  debounced(() => xhrHandler(method, url, body, startSearch, completeAction, searchError, dispatch, getState()));
};

export default refreshSearch;
