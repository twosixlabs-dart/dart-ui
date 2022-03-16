import UserService from '../../../../UserService';
import { saveToken } from '../../../dart-ui/redux/actions/dart.actions';
import { USE_DART_AUTH } from '../../../common/config/constants';

export default function xhrHandler(
  filesData,
  metaData,
  apiUrl,
  successUpload,
  setProgressUpload,
  failureUpload,
  dispatch,
  state,
) {
  const xhr = new XMLHttpRequest();
  xhr.withCredentials = true;

  const tokenString = state.dart.nav.token;
  const { idTokenObj } = state.dart.nav;

  if (USE_DART_AUTH) {
    const currentTime = new Date().getTime();
    if (idTokenObj.exp * 1000 - currentTime < 30000) {
      UserService.updateToken((newToken, newIdTokenObj) => {
        dispatch(saveToken(newToken, newIdTokenObj));
        xhrHandler(
          filesData,
          metaData,
          apiUrl,
          successUpload,
          setProgressUpload,
          failureUpload,
          dispatch,
          {
            ...state,
            dart: {
              ...state.dart,
              nav: {
                ...state.dart.nav,
                token: newToken,
                idTokenObj: newIdTokenObj,
              },
            },
          },
        );
      });

      return;
    }
  }

  // let user = JSON.parse(localStorage.getItem('user'));
  // console.log("user:", user);
  // if (user && user.authdata) {
  //     console.log("AUTH:", user.authdata);
  //     xhr.setRequestHeader("Authorizaiton", "basic" + user.authdata);
  // }
  const formData = new FormData();
  formData.append('metadata', JSON.stringify(metaData));
  formData.append('file', filesData.file);

  xhr.open('POST', apiUrl, true);
  if (USE_DART_AUTH) xhr.setRequestHeader('Authorization', `Bearer ${tokenString}`);

  xhr.upload.onprogress = (e) => {
    const percentageProgress = e.loaded === 100 ? 100 : Math.ceil((e.loaded / e.total) * 100);
    setProgressUpload(percentageProgress);
  };
  xhr.send(formData);

  // xhr.responseType = 'json';

  xhr.onload = () => {
    // const responseObj = xhr.response;
    const { status, response } = xhr;
    if (status !== 201) {
      // let errMsg = status.toString();
      // if (responseObj !== null) errMsg = responseObj.toString();
      failureUpload();
    } else {
      successUpload(JSON.parse(response));
    }
  };

  xhr.onerror = () => {
    failureUpload();
  };
}
