// import UserService from '../../../../UserService';
import { USE_DART_AUTH } from '../../config/constants';

export default function genXhrHandler({ token }) {
  return (
    method,
    url,
    body,
    startAction,
    completeAction,
    errorAction,
    dispatch,
    state,
    setProgressUpload,
    successStatus,
  ) => {
    const xhr = new XMLHttpRequest();

    const tokenString = token;

    dispatch(startAction());
    xhr.open(method, url, true);
    if (USE_DART_AUTH) xhr.setRequestHeader('Authorization', `Bearer ${tokenString}`);
    xhr.withCredentials = true;

    if (setProgressUpload) {
      xhr.upload.onprogress = (e) => {
        const percentageProgress = e.loaded === 100 ? 100 : Math.ceil((e.loaded / e.total) * 100);
        dispatch(setProgressUpload(percentageProgress));
      };
    }

    if (body === null) xhr.send();
    else xhr.send(body);

    xhr.responseType = 'json';
    xhr.onload = () => {
      const responseObj = xhr.response;
      const { status } = xhr;
      const statusCheck = successStatus || 200;
      if (status !== statusCheck) {
        let errMsg = status.toString();
        if (responseObj !== null) errMsg = responseObj;
        dispatch(errorAction(errMsg));
      } else {
        dispatch(completeAction(responseObj));
      }
    };

    xhr.ontimeout = () => {
      dispatch(errorAction('Timeout'));
    };

    xhr.onerror = () => {
      dispatch(errorAction('Network Error'));
    };
  };
}
