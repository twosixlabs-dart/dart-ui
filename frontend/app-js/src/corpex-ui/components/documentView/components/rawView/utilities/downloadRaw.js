import { USE_DART_AUTH } from '../../../../../../common/config/constants';

export default function downloadRaw(url, token) {
  return () => {
    const anchor = document.createElement('a');
    document.body.appendChild(anchor);

    const requestHeaders = new Headers();
    if (USE_DART_AUTH) requestHeaders.append('Authorization', `Bearer ${token}`);

    fetch(url, { headers: requestHeaders })
      .then((response) => {
        const { headers } = response;
        return response.blob().then((fileData) => {
          const objectUrl = window.URL.createObjectURL(fileData);

          const splitUrl = url.split('/');
          let filename = splitUrl[splitUrl.length - 1].split('?')[0];
          const disposition = headers.get('Content-Disposition');
          if (disposition) {
            const filenameRegex = /filename[^;=\n]*=['"](.*?\2|[^;\n]*)['"]/;
            const matches = filenameRegex.exec(disposition);
            if (matches != null && matches[1]) {
              [, filename] = matches;
            }
          }

          anchor.href = objectUrl;
          anchor.download = filename;
          anchor.click();

          window.URL.revokeObjectURL(objectUrl);
        });
      });
  };
}
