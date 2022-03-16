import { toPairs } from 'lodash';

// import xhrHandler from '../../../common/redux/thunk/xhrHandler';
import { DART_API_URL_BASE } from '../../config/constants';
import { poll } from '../../../common/utilities/helpers';
import {
  completePollStatus,
  setPollWindow,
  startPolling,
  startPollStatus,
  updateDocumentStatusAll,
} from '../actions/uploadFiles.actions';

function pollStatus(xhrHandler, docIdsIn) {
  return (dispatch, getState) => {
    dispatch(setPollWindow(docIdsIn));
    const isPollingCache = getState().forklift.isPolling;
    if (isPollingCache) return;
    dispatch(startPolling());

    let startingDocIds = docIdsIn;

    const urlBase = `${DART_API_URL_BASE}/status?docIds=`;
    const body = null;
    const method = 'GET';

    const startAction = startPollStatus;

    const sendRequest = () => {
      const { forklift: { polledDocuments, pollRequestPending, pollWindow } } = getState();
      const docIds = startingDocIds || pollWindow || toPairs(polledDocuments)
        .map(([docId]) => docId);

      startingDocIds = null;

      if (docIds.length < 1 || pollRequestPending) return;

      const chunk = 50;
      for (let i = 0, j = docIds.length; i < j; i += chunk) {
        const docsSlice = docIds.slice(i, i + chunk);
        const completeAction = (res) => completePollStatus(docsSlice, res);
        const errorAction = () => updateDocumentStatusAll(docsSlice, 'POLL_STATUS_FAILURE');
        const url = urlBase + docsSlice.join(',');
        // eslint-disable-next-line max-len
        xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
        // do whatever
      }
    };

    const condition = () => {
      const { forklift: { isPolling } } = getState();
      return isPolling;
    };

    const complete = () => {};

    setTimeout(() => poll(sendRequest, condition, complete, null, 2500), 100);
  };
}

export default pollStatus;
