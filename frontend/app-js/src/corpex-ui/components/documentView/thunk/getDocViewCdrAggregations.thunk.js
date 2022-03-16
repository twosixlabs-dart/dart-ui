import { completeDocViewAggregationsQuery } from '../documentView.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

function getDocViewCdrAggregations(xhrHandler, docId) {
  return (dispatch, getState) => {
    const url = `${API_URL_BASE}/cdr-aggregation/${docId}/aggregate-all`;

    const body = null;
    const method = 'GET';

    const startAction = () => ({ type: null });
    const completeAction = (res) => completeDocViewAggregationsQuery(res);
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getDocViewCdrAggregations;
