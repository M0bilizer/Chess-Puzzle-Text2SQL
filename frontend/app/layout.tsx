import type { Metadata } from 'next';
import './globals.css';
import { WebsiteIcon } from './website-icon';
import Head from 'next/head';

export const metadata: Metadata = {
  title: 'Chess Puzzle Text2SQL',
  description: 'My Front-End using Next.js',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <head>
        <WebsiteIcon />
      </head>
      <body className={`inter.className  h-screen flex flex-col`}>
        <main className="flex flex-1 flex-grow flex-col pb-12">{children}</main>
      </body>
    </html>
  );
}
