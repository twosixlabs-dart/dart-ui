// import axios from 'axios'
import uploadFilesTypes from './uploadFiles.types';

export const setUploadFiles = (files) => ({
  type: uploadFilesTypes.SET_UPLOAD_FILES,
  payload: {
    files,
  },
});

export const setUploadMetaData = (metaData) => ({
  type: uploadFilesTypes.SET_UPLOAD_METADATA,
  payload: {
    metaData,
  },
});

export const startUploadFiles = () => ({
  type: uploadFilesTypes.START_UPLOAD_FILES,
});

export const finishUploadFiles = () => ({
  type: uploadFilesTypes.FINISH_UPLOAD_FILES,
});

export const setUploadProgress = (id, progress) => ({
  type: uploadFilesTypes.SET_UPLOAD_PROGRESS,
  payload: {
    id,
    progress,
  },
});

export const successUploadFile = (id, docId) => ({
  type: uploadFilesTypes.SUCCESS_UPLOAD_FILE,
  payload: {
    id,
    docId,
  },
});

export const successUploadZipFile = (id, documents) => ({
  type: uploadFilesTypes.SUCCESS_UPLOAD_ZIP_FILE,
  payload: {
    id,
    documents,
  },
});

export const failureUploadFile = (id) => ({
  type: uploadFilesTypes.FAILURE_UPLOAD_FILE,
  payload: {
    id,
  },
});

export const startPollStatus = () => ({
  type: uploadFilesTypes.START_POLL_STATUS,
});

export const completePollStatus = (docIds, res) => ({
  type: uploadFilesTypes.COMPLETE_POLL_STATUS,
  docIds,
  res,
});

export const updateDocumentStatus = (docId, status, fileName) => ({
  type: uploadFilesTypes.UPDATE_DOCUMENT_STATUS,
  docId,
  status,
  fileName,
});

export const startPolling = () => ({
  type: uploadFilesTypes.START_POLLING,
});

export const stopPolling = () => ({
  type: uploadFilesTypes.STOP_POLLING,
});

export const updateDocumentStatusAll = (docIds, status) => ({
  type: uploadFilesTypes.UPDATE_DOCUMENT_STATUS_ALL,
  docIds,
  status,
});

export const setPollWindow = (docIds) => ({
  type: uploadFilesTypes.SET_POLL_WINDOW,
  docIds,
});

export const updateLabelsText = (newText) => ({
  type: uploadFilesTypes.UPDATE_LABELS_TEXT,
  newText,
});
