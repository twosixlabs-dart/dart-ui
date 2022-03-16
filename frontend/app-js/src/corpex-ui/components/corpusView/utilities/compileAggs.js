import toPairs from 'lodash/toPairs';
import fromPairs from 'lodash/fromPairs';

export default function compileAggs(componentIndex) {
  const aggPairs = toPairs(componentIndex).flatMap(([key, value]) => {
    const aggs = toPairs(value.aggs);
    return aggs.map(([aggKey, aggValue]) => [`${key}_${aggKey}`, aggValue]);
  });
  return fromPairs(aggPairs);
}
