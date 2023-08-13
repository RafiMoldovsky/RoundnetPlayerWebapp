import './globals.css'
import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Roundnet Player Stats',
  description: 'Find all of your roundnet stats',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>
        {children}
      </body>
    </html>
  )
}
