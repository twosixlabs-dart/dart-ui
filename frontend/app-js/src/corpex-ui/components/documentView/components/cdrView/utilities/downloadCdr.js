import downloadData from '../../../../../../common/utilities/downloadData';
import { API_URL_BASE } from '../../../../../config/constants';

export default function downloadCdr(xhrHandler, logger, docId, tenant) {
  const noOpAction = { type: 'NO_OP' };

  const tenantQuery = tenant ? `?tenant=${tenant}` : '';
  const url = `${API_URL_BASE}/documents/${docId}${tenantQuery}`;
  const filename = `${docId}.json`;

  const onComplete = (res) => {
    const stringRes = typeof (res) === 'string' ? res : JSON.stringify(res);
    downloadData(stringRes, filename, 'application/json');
    return noOpAction;
  };

  const onError = (res) => {
    const logMsg = (typeof (res) === 'string') ? res : JSON.stringify(res);
    logger.report(`Unable to retrieve cdr ${docId}`, logMsg);
  };

  return xhrHandler(
    'GET',
    url,
    '',
    noOpAction,
    onComplete,
    onError,
    () => {
    },
  );
}
