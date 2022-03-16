function checkExistence(value) {
  return value !== null && value !== undefined;
}

function fixedOrElse(orElse) {
  if (checkExistence(orElse)) return orElse;
  return null;
}

export function envStr(value, orElse) {
  if (checkExistence(value)) return value;
  return fixedOrElse(orElse);
}

export function envInt(value, orElse) {
  if (!checkExistence(value)) return fixedOrElse(orElse);
  return parseInt(value, 10);
}

export function envFloat(value, orElse) {
  if (!checkExistence(value)) return fixedOrElse(orElse);
  return parseFloat(value);
}

export function envBool(value, orElse) {
  const foe = fixedOrElse(orElse);
  if (!checkExistence(value)) return foe;
  const normalValue = value.toLowerCase().trim();
  if (normalValue === 'false') return false;
  if (normalValue === 'true') return true;
  return foe;
}

export function envArray(value, splitRegx, mapper) {
  const arrayStr = value;
  const arrayStrings = arrayStr ? arrayStr.split(splitRegx) : null;
  if (arrayStrings === null) return null;
  return mapper ? arrayStrings.map(mapper) : arrayStrings;
}
