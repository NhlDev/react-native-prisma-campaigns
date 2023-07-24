import React, { useEffect, useState } from 'react';
import {
  Text,
  NativeModules,
  Modal,
  View,
  Image,
  TouchableOpacity,
  StyleSheet,
  Linking,
  type ViewStyle,
  Platform,
} from 'react-native';
import WebView, { type WebViewMessageEvent } from 'react-native-webview';

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
  // Estado para la visualizacion de los banners tipo PopUp
  const [modalVisible, setModalVisible] = useState(true);

  // Estado para el nombre del placeholder
  const [bannerInformation, syncPlaceholder] = useState(null as any);
  useEffect(() => {
    if (props.placeholderName) {
      PrismaCampaigns.syncPage(props.placeholderName).then(
        (bannerData: PrismaBanner) => {
          syncPlaceholder(bannerData);

          if (bannerData.IsPopup && bannerData.PopUpTimeout) {
            setTimeout(() => {
              setModalVisible(false);
            }, bannerData.PopUpTimeout);
          }
        }
      );
    } else {
      syncPlaceholder(null);
    }
  }, [props.placeholderName]);

  // Funcion intermediaria para atender mensajes JS en banners HTML
  const ProccessWebViewMessage = (message: WebViewMessageEvent) => {
    switch (message.nativeEvent.data) {
      case 'StartFunnel':
        StartFunnel(bannerInformation.CampaignLink);
        break;
      case 'DismissFunnel':
        PrismaCampaigns.Dismiss(bannerInformation.TrackingToken);
        setModalVisible(false);
        break;
      case 'CloseFunnel':
        setModalVisible(false);
        break;
      default:
        console.warn('PRISMA - Unknown message: ' + message.nativeEvent.data);
    }
  };

  const StartFunnel = (url: string): void => {
    Linking.openURL(url);
  };

  // Funcion parar obtener el banner
  const createBanner = (bannerInfo?: PrismaBanner): JSX.Element => {
    return (
      <View style={{ flex: 1 }}>
        {bannerInfo != null ? (
          bannerInfo?.IsHtml ? (
            <WebView
              originWhitelist={['*']}
              source={{ html: bannerInfo.Content }}
              onMessage={ProccessWebViewMessage}
              {...props}
              onShouldStartLoadWithRequest={(request: any) => {
                if (request.url !== 'about:blank') {
                  Linking.openURL(request.url);
                  return false;
                } else return true;
              }}
            />
          ) : (
            <TouchableOpacity
              {...props}
              onPress={() => StartFunnel(bannerInfo!.CampaignLink)}
            >
              <Image
                {...(props as any)}
                source={{ uri: bannerInfo!.Content }}
              />
            </TouchableOpacity>
          )
        ) : null}
      </View>
    );
  };

  // Retorno con el banner envuelto en un modal, o sin modal
  return (
    <View {...props}>
      {bannerInformation?.IsPopup ? (
        <Modal style={{ width: '100%', height: '100%' }} visible={modalVisible}>
          <View style={styles.modalHeader}>
            {bannerInformation.PopUpShowClose ? (
              <TouchableOpacity onPress={() => setModalVisible(false)}>
                <Text style={styles.modalHeaderCloseText}>X</Text>
              </TouchableOpacity>
            ) : null}
          </View>
          {createBanner(bannerInformation)}
        </Modal>
      ) : (
        createBanner(bannerInformation)
      )}
    </View>
  );
};

interface PrismaProps {
  placeholderName: string;
  style: ViewStyle;
}

const styles = StyleSheet.create({
  modalHeader: {
    width: '100%',
    flexDirection: 'column',
    alignItems: 'flex-end',
    backgroundColor: 'black',
  },
  modalHeaderCloseText: {
    textAlign: 'center',
    paddingLeft: 5,
    paddingRight: 5,
    fontSize: 15,
  },
});

interface PrismaBanner {
  Content: string;
  IsHtml: boolean;
  IsPopup: boolean;
  PopUpTimeout: number;
  PopUpShowClose: boolean;
  CampaignLink: string;
  TrackingToken: string;
}
