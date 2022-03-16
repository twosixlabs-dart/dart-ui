import { debounce } from 'lodash';

import { completeCount, startCount } from '../../searchResults/searchResults.actions';
import { API_URL_BASE } from '../../../config/constants';

const debounced = debounce((fn) => fn(), 350);

const executeCountThunk = (xhrHandler, queries, tenant) => (dispatch, getState) => {
  const request = {
    queries,
  };

  if (tenant && tenant !== 'global') request.tenant_id = tenant;
  else {
    // eslint-disable-next-line prefer-destructuring
    const tenantId = getState().dart.nav.tenantId;
    if (tenantId && tenantId !== 'global') request.tenant_id = tenantId;
  }

  const url = `${API_URL_BASE}/search/count`;
  const body = JSON.stringify(request);
  const method = 'POST';

  // eslint-disable-next-line max-len
  debounced(() => xhrHandler(method, url, body, startCount, completeCount, () => ({ type: null }), dispatch, getState()));
};

export default executeCountThunk;
