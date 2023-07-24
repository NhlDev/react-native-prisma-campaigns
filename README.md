# react-native-prisma-campaigns

[Prisma Campaigns](https://prismacampaigns.com/) integration for React Native apps.

This Library is a brigde with Prisma's [Android SDK](https://docs.prismacampaigns.com/en/sdk/android.html) and [iOS SDK](https://docs.prismacampaigns.com/en/sdk/ios.html).

## Installation

```sh
npm install react-native-prisma-campaigns
```

### Android

You need to add the Prisma SDK dependency on you project's gradle file.
You can use the aar file bundled with this package:
```gradle
  implementation files("../../node_modules/react-native-prisma-campaigns/android-prisma-sdk/prisma-sdk.aar")
```
Or [download](http://docs.prismacampaigns.com/sdk/app-release.aar) the .AAR library and reference the downloaded file.

#### iOS

No futher configuration required.

## Usage

Before starting to show Prisma banners, you need to call the **PrismaLoad** method:
```js
import { PrismaLoad } from 'react-native-prisma-campaigns';

// ...

export default function App() {

    const PRISMA_URL: string         = "<YOUR PRISMA URL>"
    const PRISMA_PORT: string        = "<PORT>" 
    const PRISMA_APP_TOKEN: string   = "<YOUR_APPTOKEN>" 
    const PRISMA_CUSTOMER_ID: string = "<CUSTOMER_ID>"

    PrismaLoad(PRISMA_URL, PRISMA_PORT, PRISMA_APP_TOKEN, PRISMA_CUSTOMER_ID);
//...
```
| Parameters            | Description                                                               | Example                           |
| ----------------------|:--------------------------------------------------------------------------| ----------------------------------|
| PRISMA_URL            | This is the URL to you Prisma instance                                    | `'university.prismacampaigns.com'`|
| PRISMA_PORT           | The port used by you Prisma instance                                      | `'443'`                           |
| PRISMA_APP_TOKEN      | You application token configured on Prisma                                | `'00000-000-0000-00-00000000000'` |
| PRISMA_CUSTOMER_ID    | The Customer ID requesting campaigns (empty string(`''`) if is annonymous)| `'CustomerName'`                  |


Once you initialized the library, you can start displaying the banners
You need to use the UI component `*PrismaPlaceholder*` for rendering the banners:

```js

//...

 return (
    <PrismaPlaceholder style={styles.placeholderStyles} placeholderName='PopUp-Banner Web'>
    </PrismaPlaceholder>
  );

}

```

Where the prop `placeholderName` is the name of the placeholder required to render.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
