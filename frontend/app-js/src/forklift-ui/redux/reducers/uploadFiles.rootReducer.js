import uploadFilesTypes from '../actions/uploadFiles.types';
import setFilesData from '../../utilities/uploadFiles.utils';

const INITIAL_STATE = {

  polledDocuments: {},
  isPolling: false,
  pollRequestPending: false,
  currentFileUploading: -1,
  isUploading: false,
  numOfFilesSuccessUpload: 0,
  numOfFilesFailedUpload: 0,
  metaData: {
    tenants: [],
    team: '',
    labels: [],
    genre: 'Unspecified',
  },
  labelsText: '',
  filesRaw: '',
  filesData: {},

  // format will be like below
  // 1: {
  //   id: 1,
  //   file,
  //   progress: 0,
  //   cancelSource: source,
  //   status: 0,
  //   docId: undefined / string,
  // },
};

const uploadFilesRootReducer = (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case uploadFilesTypes.SET_UPLOAD_FILES: {
      const polledDocuments = { ...state.polledDocuments };
      Object.keys(polledDocuments)
        .forEach((id) => { polledDocuments[id].isCurrentUpload = false; });
      return {
        ...state,
        ...polledDocuments,
        isUploading: false,
        currentFileUploading: -1,
        numOfFilesSuccessUpload: 0,
        numOfFilesFailedUpload: 0,
        filesRaw: action.payload.files,
        filesData: setFilesData(action.payload.files),
      };
    }

    case uploadFilesTypes.SET_UPLOAD_METADATA: {
      return {
        ...state,
        metaData: action.payload.metaData,
      };
    }

    case uploadFilesTypes.START_UPLOAD_FILES: {
      return {
        ...state,
        currentFileUploading: 0,
        numOfFilesSuccessUpload: 0,
        numOfFilesFailedUpload: 0,
        isUploading: true,
        currentUploadingFileIndex: 0,
      };
    }

    case uploadFilesTypes.FINISH_UPLOAD_FILES:
      return {
        ...state,
        isUploading: false,
      };

    case uploadFilesTypes.SET_UPLOAD_PROGRESS:
      return {
        ...state,
        filesData: {
          ...state.filesData,
          [action.payload.id]: {
            ...state.filesData[action.payload.id],
            progress: action.payload.progress,
          },
        },
      };

    case uploadFilesTypes.SUCCESS_UPLOAD_FILE: {
      const filesObj = state.filesRaw === '' ? {} : { ...state.filesRaw };
      const fileName = state.filesData[action.payload.id].file.name;
      Reflect.deleteProperty(filesObj, action.payload.id);
      const newFilesRaw = Object.keys(filesObj).length > 0 ? filesObj : '';

      return {
        ...state,
        currentFileUploading: state.currentFileUploading + 1,
        numOfFilesSuccessUpload: state.numOfFilesSuccessUpload + 1,
        polledDocuments: {
          ...state.polledDocuments,
          [action.payload.docId]: {
            status: 'Uploaded',
            fileName,
            isCurrentUpload: true,
            timestamp: Number(new Date()),
          },
        },
        filesRaw: newFilesRaw,
        filesData: {
          ...state.filesData,
          [action.payload.id]: {
            ...state.filesData[action.payload.id],
            status: 1,
            docId: action.payload.docId,
          },
        },
      };
    }

    case uploadFilesTypes.SUCCESS_UPLOAD_ZIP_FILE: {
      const filesObj = state.filesRaw === '' ? {} : { ...state.filesRaw };
      Reflect.deleteProperty(filesObj, action.payload.id);
      const newFilesRaw = Object.keys(filesObj).length > 0 ? filesObj : '';
      const documentsToPoll = {};
      action.payload.documents.forEach((document) => {
        documentsToPoll[document.document_id] = {
          status: 'Uploaded',
          fileName: document.filename,
          isCurrentUpload: true,
          timestamp: Number(new Date()),
        };
      });
      return {
        ...state,
        currentFileUploading: state.currentFileUploading + 1,
        numOfFilesSuccessUpload: state.numOfFilesSuccessUpload + 1,
        polledDocuments: {
          ...state.polledDocuments,
          ...documentsToPoll,
        },
        filesRaw: newFilesRaw,
        filesData: {
          ...state.filesData,
          [action.payload.id]: {
            ...state.filesData[action.payload.id],
            status: 1,
          },
        },
      };
    }

    case uploadFilesTypes.FAILURE_UPLOAD_FILE:
      return {
        ...state,
        currentFileUploading: state.currentFileUploading + 1,
        numOfFilesFailedUpload: state.numOfFilesFailedUpload + 1,
        filesData: {
          ...state.filesData,
          [action.payload.id]: {
            ...state.filesData[action.payload.id],
            status: 2,
          },
        },
      };

    case uploadFilesTypes.START_POLL_STATUS: {
      return {
        ...state,
        pollRequestPending: true,
      };
    }

    case uploadFilesTypes.COMPLETE_POLL_STATUS: {
      const newPolledDocuments = { ...state.polledDocuments };
      const { res } = action;
      if (res) {
        const resKeys = Object.keys(res);
        for (let i = 0; i < resKeys.length; i += 1) {
          const newStatus = res[resKeys[i]].dart;
          if (resKeys[i] in newPolledDocuments) {
            newPolledDocuments[resKeys[i]].status = newStatus;
          } else {
            newPolledDocuments[resKeys[i]] = {
              status: newStatus,
              timestamp: Number(new Date()),
              fileName: res[resKeys[i]].source_uri,
            };
          }
        }
      }

      return {
        ...state,
        pollRequestPending: false,
        polledDocuments: newPolledDocuments,
      };
    }

    case uploadFilesTypes.UPDATE_DOCUMENT_STATUS: {
      const newPolledDocuments = { ...state.polledDocuments };
      const { docId, status, fileName } = action;
      if (docId in newPolledDocuments && newPolledDocuments[docId].status) {
        newPolledDocuments[docId].status = status;
      } else {
        newPolledDocuments[docId] = {
          status,
          fileName,
        };
      }
      if (fileName) newPolledDocuments[docId].fileName = fileName;

      return {
        ...state,
        polledDocuments: newPolledDocuments,
      };
    }

    case uploadFilesTypes.START_POLLING: {
      return {
        ...state,
        isPolling: true,
      };
    }

    case uploadFilesTypes.STOP_POLLING: {
      return {
        ...state,
        isPolling: false,
      };
    }

    case uploadFilesTypes.UPDATE_DOCUMENT_STATUS_ALL: {
      const newPolledDocuments = { ...state.polledDocuments };
      action.docIds.forEach((id) => {
        const newStatus = action.status;
        if (action.status) newPolledDocuments[id].status = newStatus;
      });

      return {
        ...state,
        pollRequestPending: false,
        polledDocuments: newPolledDocuments,
      };
    }

    case uploadFilesTypes.SET_POLL_WINDOW: {
      return {
        ...state,
        pollWindow: action.docIds,
      };
    }

    case uploadFilesTypes.UPDATE_LABELS_TEXT: {
      return {
        ...state,
        labelsText: action.newText,
      };
    }

    default:
      return state;
  }
};

export default uploadFilesRootReducer;
