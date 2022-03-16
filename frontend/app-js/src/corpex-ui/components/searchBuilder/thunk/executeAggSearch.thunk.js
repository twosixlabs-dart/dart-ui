import { toPairs, debounce } from 'lodash';

import { searchError, completeAggSearch, startSearch } from '../../searchResults/searchResults.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

const debounced = debounce((fn) => fn(), 200);

function executeAggSearchThunk(xhrHandler, queries, commonAggs, privateAggs, tenant) {
  return (dispatch, getState) => {
    const aggs = {};
    if (commonAggs) toPairs(commonAggs).forEach(([label, query]) => { aggs[label] = query; });
    if (privateAggs) toPairs(privateAggs).forEach(([label, query]) => { aggs[label] = query; });

    const request = {
      page: 0,
      page_size: 0,
      queries,
      aggregations: aggs,
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

    const completeAction = (res) => completeAggSearch(res, commonAggs);

    // eslint-disable-next-line max-len
    debounced(() => xhrHandler(method, url, body, startSearch, completeAction, searchError, dispatch, getState()));
  };
}

export default executeAggSearchThunk;
