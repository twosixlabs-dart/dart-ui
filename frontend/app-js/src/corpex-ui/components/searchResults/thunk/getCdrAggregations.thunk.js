import { completeAggregationsQuery } from '../searchResults.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

function getCdrAggregations(xhrHandler, resIndex, docId) {
  return (dispatch, getState) => {
    const url = `${API_URL_BASE}/cdr-aggregation/${docId}/aggregate-all`;

    const body = null;
    const method = 'GET';

    const startAction = () => ({ type: null });
    const completeAction = (res) => completeAggregationsQuery(resIndex, docId, res);
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getCdrAggregations;
