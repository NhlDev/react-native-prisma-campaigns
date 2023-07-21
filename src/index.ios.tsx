import React from 'react';
import { NativeModules, View, Platform, type ViewStyle } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-prisma-campaigns' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const PrismaCampaigns = NativeModules.PrismaCampaigns
  ? NativeModules.PrismaCampaigns
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export const PrismaLoad = (
  Server: string,
  Port: string,
  AppToken: string,
  CustomerId: string
) => {
  PrismaCampaigns.Load(Server, Port, AppToken, CustomerId);
};

export const PrismaPlaceholder = (props: PrismaProps) => {
  // Retorno con el banner envuelto en un modal, o sin modal
  return <View {...props} />;
};

interface PrismaProps {
  placeholderName: string;
  style: ViewStyle;
}
