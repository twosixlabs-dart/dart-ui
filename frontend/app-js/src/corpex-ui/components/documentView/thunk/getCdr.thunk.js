import { completeGetCdr, docViewGetCdrFailure } from '../documentView.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';
import { clearCdrView } from '../components/cdrView/cdrView.actions';

function getCdr(xhrHandler, docId) {
  return (dispatch, getState) => {
    const tenantIdorNull = getState().dart.nav.tenantId;
    const tenantQuery = tenantIdorNull ? `?tenant=${tenantIdorNull}` : '';
    const url = `${API_URL_BASE}/documents/${docId}${tenantQuery}`;

    const body = null;
    const method = 'GET';

    const startAction = () => clearCdrView();
    const completeAction = (res) => completeGetCdr(res);
    const errorAction = (res) => docViewGetCdrFailure(res);

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getCdr;
