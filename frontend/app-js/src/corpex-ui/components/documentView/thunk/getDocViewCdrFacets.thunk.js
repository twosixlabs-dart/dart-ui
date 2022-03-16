import { completeDocViewFacetsQuery } from '../documentView.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

function getDocViewCdrFacets(xhrHandler, docId) {
  return (dispatch, getState) => {
    const tenantIdorNull = getState().dart.nav.tenantId;
    const tenantQuery = tenantIdorNull ? `&tenant=${tenantIdorNull}` : '';
    const url = `${API_URL_BASE}/documents/${docId}?fieldsIncl=annotations${tenantQuery}`;

    const body = null;
    const method = 'GET';

    const startAction = () => ({ type: null });
    const completeAction = (res) => completeDocViewFacetsQuery(res);
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getDocViewCdrFacets;
