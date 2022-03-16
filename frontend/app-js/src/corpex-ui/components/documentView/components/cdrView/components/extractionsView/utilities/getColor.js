import { toPairs } from 'lodash';

const correctAlpha = (color, isFocus, isHover) => {
  const oldAlpha = color[3];
  let newAlpha = isHover ? (1 + oldAlpha) / 2.5 : oldAlpha;
  if (isFocus) newAlpha = (1 + oldAlpha) / 1.5;
  return `rgba(${color[0]},${color[1]},${color[2]},${newAlpha})`;
};

const getColor = (typeStack, checkedTagTypes, isFocus, isHover) => {
  const colors = toPairs(checkedTagTypes)
    .filter((ele) => typeStack.includes(ele[0]))
    .map((ele) => [...ele[1], 0.25]);

  if (colors.length === 1) return correctAlpha(colors[0], isFocus, isHover);

  // TODO: blend colors
  return correctAlpha(colors[0], isFocus, isHover);
};

export default getColor;
