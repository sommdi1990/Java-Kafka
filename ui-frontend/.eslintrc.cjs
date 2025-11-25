/* eslint config for React + TypeScript (Vite) */

module.exports = {
    root: true,
    env: {
        browser: true,
        es2021: true,
        node: true,
    },
    parser: "@typescript-eslint/parser",
    parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        ecmaFeatures: {
            jsx: true,
        },
        project: "./tsconfig.json",
        tsconfigRootDir: __dirname,
    },
    settings: {
        react: {
            version: "detect",
        },
    },
    plugins: [
        "react-refresh",
        "react-hooks",
        "@typescript-eslint",
    ],
    extends: [
        "eslint:recommended",
        "plugin:@typescript-eslint/recommended",
        "plugin:react-hooks/recommended",
    ],
    rules: {
        "react-refresh/only-export-components": "off",
        "no-useless-catch": "off",
        "@typescript-eslint/no-non-null-assertion": "off",
        "@typescript-eslint/no-explicit-any": "off",
        "@typescript-eslint/no-unused-vars": ["warn", {argsIgnorePattern: "^_", varsIgnorePattern: "^_"}],
    },
    ignorePatterns: [
        "dist",
        "node_modules",
        "vite.config.ts",
    ],
};


