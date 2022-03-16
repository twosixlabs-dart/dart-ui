// import {
//   author,
//   extractedText,
//   pubDate,
//   title,
// } from '../../../../../utilities/cdrReader';

const maxChunkLength = 6000;

export default function processExtractedText(cdr) {
  const chunkArray = [{ paragraph: true }, { paragraph: true }];
  const text = cdr.extracted_text;
  const au = cdr.extracted_metadata.Author || '';
  const ti = cdr.extracted_metadata.Title || '';
  const dt = cdr.extracted_metadata.CreationDate || '';
  if (au !== '' || ti !== '' || dt !== '') {
    chunkArray.push({
      header: true,
      title: ti,
      author: au,
      date: dt,
    });
    chunkArray.push({ paragraph: true });
    chunkArray.push({ paragraph: true });
  }

  let currentOffset = 0;
  let i = 0;
  let last = {};
  let span = '';
  while (i < text.length) {
    if (text[i] === '\n') {
      if (span.length > 0) {
        const chunk = {
          text: span,
          length: span.length,
          offset: currentOffset,
        };
        chunkArray.push(chunk);
        last = chunk;
        span = '';
      }

      i += 1;
      if (!last.paragraph) {
        chunkArray.push({ paragraph: true });
        last = { paragraph: true };
        while (text[i] === ' ') i += 1;
      }
      currentOffset = i;
    } else {
      span += text[i];
      i += 1;
      if (span.length >= maxChunkLength) {
        const chunk = {
          text: span,
          length: span.length,
          offset: currentOffset,
        };
        chunkArray.push(chunk);
        last = chunk;
        span = '';
        currentOffset = i;
      }
    }
  }

  if (span.length > 0) {
    const chunk = {
      text: span,
      length: span.length,
      offset: currentOffset,
    };
    chunkArray.push(chunk);
  }

  return chunkArray;
}
