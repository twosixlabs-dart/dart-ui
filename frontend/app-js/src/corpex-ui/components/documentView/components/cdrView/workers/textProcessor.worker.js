import processExtractedText from '../utilities/processExtractedText';

onmessage = function textProcessor(evt) {
  const textArray = processExtractedText(evt.data);
  postMessage(textArray);
};
