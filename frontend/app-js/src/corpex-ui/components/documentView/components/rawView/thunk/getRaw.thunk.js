import { completeGetRaw } from '../rawView.actions';
// import xhrHandler from '../../../../../../common/redux/thunk/xhrHandler';
import { RAW_DOC_URL, RAW_DOC_SOURCE } from '../../../../../config/constants';

function getRaw(xhrHandler, docId) {
  return (dispatch, getState) => {
    const tenantIdorNull = getState().dart.nav.tenantId;
    const tenantQuery = tenantIdorNull ? `?tenantId=${tenantIdorNull}` : '';
    let url = `${RAW_DOC_URL}/${docId}${tenantQuery}`;
    if (RAW_DOC_SOURCE === 'cauoogle') url = `${RAW_DOC_URL}/${docId}/raw`;
    if (RAW_DOC_SOURCE === 'static') url = RAW_DOC_URL;

    const body = null;
    const method = 'GET';

    const startAction = () => ({ type: null });
    const completeAction = (res) => {
      completeGetRaw(res);
    };
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getRaw;
