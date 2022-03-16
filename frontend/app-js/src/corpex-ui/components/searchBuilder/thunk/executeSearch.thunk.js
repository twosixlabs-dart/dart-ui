import { debounce } from 'lodash';
import { searchError, completeSearch, startSearch } from '../../searchResults/searchResults.actions';

// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE, DEFAULT_SEARCH_PAGE_SIZE } from '../../../config/constants';

const debounced = debounce((fn) => fn(), 350);

const executeSearch = (xhrHandler, queries, aggs, tenant) => (dispatch, getState) => {
  const request = aggs ? {
    page: 0,
    page_size: DEFAULT_SEARCH_PAGE_SIZE,
    fields: ['cdr.document_id', 'word_count', 'cdr.source_uri', 'cdr.extracted_metadata.CreationDate', 'cdr.extracted_metadata.Subject', 'cdr.extracted_metadata.Title', 'cdr.team', 'cdr.capture_source', 'cdr.content_type', 'cdr.extracted_metadata.Type', 'cdr.extracted_metadata.Description', 'cdr.extracted_metadata.Language', 'extracted_metadata.Classification', 'cdr.extracted_metadata.Publisher', 'cdr.extracted_metadata.Pages', 'cdr.extracted_metadata.Author', 'cdr.extracted_metadata.Creator', 'cdr.extracted_metadata.Producer', 'cdr.timestamp'],
    queries,
    aggregations: aggs,
  } : {
    page: 0,
    page_size: DEFAULT_SEARCH_PAGE_SIZE,
    fields: ['cdr.document_id', 'word_count', 'cdr.source_uri', 'cdr.extracted_metadata.CreationDate', 'cdr.extracted_metadata.Subject', 'cdr.extracted_metadata.Title', 'cdr.team', 'cdr.capture_source', 'cdr.content_type', 'cdr.extracted_metadata.Type', 'cdr.extracted_metadata.Description', 'cdr.extracted_metadata.Language', 'extracted_metadata.Classification', 'cdr.extracted_metadata.Publisher', 'cdr.extracted_metadata.Pages', 'cdr.extracted_metadata.Author', 'cdr.extracted_metadata.Creator', 'cdr.extracted_metadata.Producer', 'cdr.timestamp'],
    queries,
  };

  if (tenant && tenant !== 'global') request.tenant_id = tenant;
  else {
    // eslint-disable-next-line prefer-destructuring
    const tenantId = getState().dart.nav.tenantId;
    if (tenantId && tenantId !== 'global') request.tenant_id = tenantId;
  }

  const url = `${API_URL_BASE}/search`;
  const body = JSON.stringify(request);
  const method = 'POST';

  const completeAction = (res) => completeSearch(res, queries, aggs);

  // eslint-disable-next-line max-len
  debounced(() => xhrHandler(method, url, body, startSearch, completeAction, searchError, dispatch, getState()));
};

export default executeSearch;
