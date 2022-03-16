import { size, uniq } from 'lodash';
// import xhrHandler from './xhrHandler';
import * as uploadFilesActions from '../actions/uploadFiles.actions';
import { finishUploadFiles } from '../actions/uploadFiles.actions';
import { API_URL_BASE } from '../../config/constants';

function uploadFiles(xhrHandler) {
  return (dispatch, getState) => {
    const { forklift } = getState();
    const { filesData } = forklift;
    const { metaData } = forklift;
    const { currentFileUploading } = forklift;

    if (Object.entries(filesData).length === 0) return;

    if (currentFileUploading < 0) return;

    if (currentFileUploading === size(filesData)) {
      dispatch(finishUploadFiles());
      return;
    }

    const fileData = filesData[currentFileUploading];
    const fileMetadata = {};
    Object.keys(metaData).forEach((field) => {
      if (metaData[field] === null
        || metaData[field] === undefined
        || metaData[field] === ''
        || metaData[field].length === 0) return;
      if (field === 'labels') {
        fileMetadata[field] = uniq(metaData[field]).filter((value) => value);
      } else fileMetadata[field] = metaData[field];
    });

    const successUpload = (res) => {
      const docId = res.document_id;
      return uploadFilesActions.successUploadFile(currentFileUploading, docId);
    };
    // eslint-disable-next-line max-len
    const setProgressUpload = (percentageProgress) => {
      dispatch(uploadFilesActions.setUploadProgress(currentFileUploading, percentageProgress));
    };

    const failureUpload = () => uploadFilesActions.failureUploadFile(currentFileUploading);
    const successZipUpload = (res) => {
      const { documents } = res;
      return uploadFilesActions.successUploadZipFile(currentFileUploading, documents);
    };

    const fileType = fileData.file.name.split('.').pop();

    const formData = new FormData();
    formData.append('metadata', JSON.stringify(metaData));
    formData.append('file', fileData.file);

    if (fileType === 'zip') {
      xhrHandler(
        'POST',
        `${API_URL_BASE}/upload/zip`,
        formData,
        () => ({ type: '' }),
        successZipUpload,
        failureUpload,
        dispatch,
        getState(),
        setProgressUpload,
        201,
      );
      // eslint-disable-next-line max-len
      // xhrHandler(fileData, fileMetadata, `${API_URL_BASE}/upload/zip`, successZipUpload, setProgressUpload, failureUpload, dispatch, getState());
    } else {
      // eslint-disable-next-line max-len
      // xhrHandler(fileData, fileMetadata, `${API_URL_BASE}/upload`, successUpload, setProgressUpload, failureUpload, dispatch, getState());
      xhrHandler(
        'POST',
        `${API_URL_BASE}/upload`,
        formData,
        () => ({ type: '' }),
        successUpload,
        failureUpload,
        dispatch,
        getState(),
        setProgressUpload,
        201,
      );
    }
  };
}

export default uploadFiles;
