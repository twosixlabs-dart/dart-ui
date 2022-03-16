import compileAggs from './compileAggs';

test('compileAggs merge aggs of difference components', () => {
  const testIndex = {
    component1: {
      aggs: {
        aggs1: {
          testField: 'something',
          anotherField: 5,
        },
        aggs2: {
          testField: 10,
          anotherField: [1, 2],
        },
      },
    },
    component2: {
      aggs: {
        aggs1: {
          fieldTest: 'whatever',
        },
        aggs2: {
          fieldAnother: 5,
        },
      },
    },
  };

  const compiledAggs = compileAggs(testIndex);
  expect(compiledAggs).toMatchObject({
    component1_aggs1: {
      testField: 'something',
      anotherField: 5,
    },
    component1_aggs2: {
      testField: 10,
      anotherField: [1, 2],
    },
    component2_aggs1: {
      fieldTest: 'whatever',
    },
    component2_aggs2: {
      fieldAnother: 5,
    },
  });
});
