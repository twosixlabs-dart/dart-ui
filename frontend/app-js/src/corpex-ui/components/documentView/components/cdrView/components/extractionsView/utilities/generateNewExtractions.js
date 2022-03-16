export default function generateNewExtractions(
  extrType,
  tagType,
  offsets,
  textArray,
  oldExtractions,
) {
  const newExtractions = {
    tagMarkers: { ...oldExtractions.tagMarkers },
    tagIndex: { ...oldExtractions.tagIndex },
  };

  let octr = 0;
  // eslint-disable-next-line arrow-body-style
  const sOffs = offsets
    .sort(([a], [b]) => (a <= b ? -1 : 1))
    .flatMap(([start, end], i) => {
      const extrTagId = `${extrType}_${tagType}_${start}`;
      if (newExtractions.tagIndex[extrTagId]) {
        newExtractions.tagIndex[extrTagId].extr = i;
      } else {
        newExtractions.tagIndex[extrTagId] = {
          text: null,
          extr: i,
          state: {},
        };
      }
      return [
        { opening: true, offset: start },
        { opening: false, offset: end, startingOffset: start },
      ];
    })
    .sort((a, b) => (a.offset <= b.offset ? -1 : 1));

  const markersArray = [];

  for (let i = 0; i < textArray.length; i += 1) {
    const chunk = textArray[i];
    if (chunk.paragraph) {
      markersArray.push([]);
      // eslint-disable-next-line no-continue
      continue;
    }
    if (octr >= sOffs.length || sOffs[octr] == null) {
      markersArray.push([]);
      // eslint-disable-next-line no-continue
      continue;
    }
    const chunkMarkers = [];
    while (octr < sOffs.length
            && sOffs[octr].offset < chunk.offset + chunk.length) {
      if (sOffs[octr].offset >= chunk.offset) {
        chunkMarkers.push({
          relativeOffset: sOffs[octr].offset - chunk.offset,
          opening: sOffs[octr].opening,
          startingOffset: sOffs[octr].startingOffset - chunk.offset,
          tagType,
          extrType,
        });
        if (sOffs[octr].opening) {
          const extrTagId = `${extrType}_${tagType}_${sOffs[octr].offset}`;
          if (newExtractions.tagIndex[extrTagId]) {
            newExtractions.tagIndex[extrTagId].text = i;
          } else {
            newExtractions.tagIndex[extrTagId] = {
              text: i,
              extr: null,
              state: {},
            };
          }
        }
      }

      octr += 1;
    }
    markersArray.push(chunkMarkers);
  }

  newExtractions.tagMarkers[`${extrType}_${tagType}`] = markersArray;

  return newExtractions;
}
