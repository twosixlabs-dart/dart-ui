const setFilesData = (files) => {
  let filesToUpload = {};

  for (let id = 0; id < files.length; id += 1) {
    // const CancelToken = axios.CancelToken  --> can be used for cancelling upload progress
    // const source = CancelToken.source()

    filesToUpload = {
      ...filesToUpload,
      [id]: {
        id,
        file: files[id],
        progress: 0,
        status: 0,
        // cancelSource: source,
      },
    };
  }
  return filesToUpload;
};

export default setFilesData;
