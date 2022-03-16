import { completeFacetsQuery } from '../searchResults.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

function getCdrFacetsThunk(xhrHandler, resIndex, docId) {
  return (dispatch, getState) => {
    const tenantIdOrNull = getState().dart.nav.tenantId;
    const tenantQuery = tenantIdOrNull ? `&tenant=${tenantIdOrNull}` : '';
    const url = `${API_URL_BASE}/documents/${docId}?fieldsIncl=annotations${tenantQuery}`;

    const body = null;
    const method = 'GET';

    const startAction = () => ({ type: null });
    const completeAction = (res) => completeFacetsQuery(resIndex, docId, res);
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getCdrFacetsThunk;
