import * as React from 'react';

import { StyleSheet } from 'react-native';
import { PrismaLoad, PrismaPlaceholder } from 'react-native-prisma-campaigns';

export default function App() {

  PrismaLoad(
    'university.prismacampaigns.com',
    '443',
    '30674681-9f09-4a73-b01e-db2e0ba66d7b',
    'nalderete'
  );


  return (
    <PrismaPlaceholder style={styles.placeholderStyles} placeholderName='PopUp-Banner Web'>
    </PrismaPlaceholder>
  );
}

const styles = StyleSheet.create({
  placeholderStyles: {
    alignContent: "center",
    width: '100%',
    height: 200
  }
});
