/*
 * This work was developed by Two Six Technologies for DARPA under contract #W911NF-19-C-0080.
 * (c) 2021 Two Six Technologies. All rights reserved.
 */

export default function downloadData(data, filename, type) {
  const anchor = document.createElement('a');
  document.body.appendChild(anchor);
  const blob = typeof (data) === 'string' ? new Blob([data], { type: type || 'text/plain' }) : data;
  const objectUrl = window.URL.createObjectURL(blob);
  anchor.href = objectUrl;
  anchor.download = filename;
  anchor.click();
  window.URL.revokeObjectURL(objectUrl);
  anchor.remove();
}
