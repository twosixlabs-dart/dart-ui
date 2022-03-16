import { completeFacetQuery } from '../searchBuilder.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

function getFacet(xhrHandler, componentId, facet) {
  return (dispatch, getState) => {
    const url = `${API_URL_BASE}/annotations/facets/${facet}`;
    const method = 'GET';
    const body = null;

    const startAction = () => ({ type: null });
    const completeAction = (res) => completeFacetQuery(componentId, res);
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getFacet;
